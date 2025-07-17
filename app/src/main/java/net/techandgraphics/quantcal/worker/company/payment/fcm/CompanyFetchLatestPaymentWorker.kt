package net.techandgraphics.quantcal.worker.company.payment.fcm

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
import net.techandgraphics.quantcal.R
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.broadcasts.company.CompanyFcmNotificationActionReceiver
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.defaultDateTime
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toPaymentUiModel
import net.techandgraphics.quantcal.getAccount
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.notification.NotificationType
import net.techandgraphics.quantcal.notification.NotificationUiModel
import net.techandgraphics.quantcal.services.company.CompanyFcmNotificationAction
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyFetchLatestPaymentWorker @AssistedInject constructor(
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
            payments.onVerificationEvent()
          }
        return
      }
    throw IllegalStateException()
  }

  companion object {
    const val PAYMENT_ID = "paymentId"
  }

  private suspend fun List<PaymentEntity>.onVerificationEvent() {
    map { it.toPaymentUiModel() }
      .forEach { payment ->
        val account = database.accountDao.get(payment.accountId).toAccountUiModel()
        val method = database.paymentMethodDao.get(payment.paymentMethodId)
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
        val months = database.paymentMonthCoveredDao.getByPaymentId(payment.id)
        val notification = NotificationUiModel(
          type = NotificationType.PaymentVerification,
          title = "Payment Request Verification",
          body = "${account.toFullName()} has sent a payment request",
          style = NotificationCompat.BigTextStyle().bigText(
            "${account.toFullName()} has sent a payment request " +
              "of ${months.size.times(plan.fee).toAmount()} " +
              "using ${gateway.name} " +
              "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()}.",
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
          .withActions(
            NotificationCompat.Action(R.drawable.ic_check_circle, "Approve", approveIntent),
            NotificationCompat.Action(R.drawable.ic_eye, "View", verifyIntent),
            notification = notification,
            notificationId = payment.id,
          )
      }
  }
}

private const val WORKER_UUID = "1c35dd3b-2ab6-49dd-8db7-c68fb6f32d24"

fun Context.scheduleCompanyFetchLatestPaymentWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyFetchLatestPaymentWorker>()
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
