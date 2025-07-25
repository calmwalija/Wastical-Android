package net.techandgraphics.wastical.worker.client.payment.fcm

import android.accounts.AccountManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.broadcasts.company.CompanyFcmNotificationActionReceiver
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toPaymentUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getTimeOfDay
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.notification.NotificationUiModel
import net.techandgraphics.wastical.services.company.CompanyFcmNotificationAction
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.worker.company.payment.fcm.CompanyFetchLatestPaymentWorker.Companion.PAYMENT_ID
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientFetchLatestPaymentWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      database.withTransaction { invoke() }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend operator fun invoke() {
    authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        val epochSecond = database.paymentDao.getByUpdatedAtLatest()?.updatedAt ?: -1
        val responses = paymentApi.fetchLatest(account.id, epochSecond)
        responses.payments?.map { it.toPaymentEntity() }
          ?.let { payments ->
            database.paymentDao.insert(payments)
            responses.paymentMonthsCovered
              ?.map { it.toPaymentMonthCoveredEntity() }
              ?.also {
                database.paymentMonthCoveredDao.insert(it)
              }
            payments.onFetchEvent()
          }
        return
      }
    throw IllegalStateException()
  }

  private suspend fun List<PaymentEntity>.onFetchEvent() {
    map { it.toPaymentUiModel() }
      .forEach { payment ->
        val account = database.accountDao.get(payment.accountId).toAccountUiModel()
        val method = database.paymentMethodDao.get(payment.paymentMethodId)
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
        val months = database.paymentMonthCoveredDao.getByPaymentId(payment.id)
        val theAmount = months.size.times(plan.fee).toAmount()
        val notification = NotificationUiModel(
          type = NotificationType.PaymentFetchLatest,
          title = "Payment Verification",
          body = "Your payment of $theAmount has been ${payment.status.name}.",
          style = NotificationCompat.BigTextStyle().bigText(
            "Howdy ${account.toFullName()}, your payment of $theAmount " +
              "using ${gateway.name} " +
              "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()} " +
              "you sent for verification has been ${payment.status.name}. " +
              "Thank you for your patience. " +
              "Have a good ${getTimeOfDay()}",
          ),
        )

        val intent = Intent(context, CompanyFcmNotificationActionReceiver::class.java)
        intent.putExtra(PAYMENT_ID, payment.id)

        val approveIntent = PendingIntent.getBroadcast(
          context,
          0,
          intent.also { it.action = CompanyFcmNotificationAction.Approve.name },
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val verifyIntent = PendingIntent.getBroadcast(
          context,
          1,
          intent.also { it.action = CompanyFcmNotificationAction.Verify.name },
          PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        NotificationBuilder(context)
          .show(
            notification = notification,
            notificationId = payment.id,
          )
      }
  }
}

private const val WORKER_UUID = "085afec2-116d-453e-9a4b-e06ac8e57d45"

fun Context.scheduleClientFetchLatestPaymentWorker() {
  val workRequest = OneTimeWorkRequestBuilder<ClientFetchLatestPaymentWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
