package net.techandgraphics.wastical.worker.company.payment.fcm

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
import net.techandgraphics.wastical.R
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.broadcasts.company.CompanyFcmNotificationActionReceiver
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.notification.pendingIntent
import net.techandgraphics.wastical.services.company.CompanyFcmNotificationAction
import net.techandgraphics.wastical.theAmount
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
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
      onDoWork()
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend fun onDoWork() {
    authenticatorHelper.getAccount(accountManager)
      ?.let { internalAccount ->

        val epochSecond = database.paymentDao.getByUpdatedAtLatest()?.updatedAt ?: -1
        val newValue = paymentApi.fetchLatest(internalAccount.id, epochSecond)

        val newPayments =
          newValue.payments?.map { it.toPaymentEntity() } ?: throw IllegalStateException()
        database.withTransaction {
          database.paymentDao.upsert(newPayments)
          newValue.paymentMonthsCovered
            ?.map { it.toPaymentMonthCoveredEntity() }
            ?.also {
              database.paymentMonthCoveredDao.upsert(it)
            }
          showNotification(newPayments)
        }
      } ?: throw IllegalStateException()
  }

  private suspend fun showNotification(newPayments: List<PaymentEntity>) {
    newPayments.forEach { payment ->
      val account = database.accountDao.get(payment.accountId).toAccountUiModel()
      val method = database.paymentMethodDao.get(payment.paymentMethodId)
      val theAmount = database.theAmount(payment).toAmount()
      val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)

      val theBody = "${account.toFullName()} sent a proof of payment"
      val bigText =
        "${account.toFullName()} has sent a proof of payment request of $theAmount " +
          "using ${gateway.name} " +
          "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()}"

      val toNotifModel = NotificationBuilderModel(
        type = NotificationType.PROOF_OF_PAYMENT_COMPANY_VERIFY,
        title = theBody,
        body = bigText,
        style = NotificationCompat.BigTextStyle().bigText(bigText),
        contentIntent = pendingIntent(context, GOTO_VERIFY),
      )

      val intent = Intent(context, CompanyFcmNotificationActionReceiver::class.java)
      intent.putExtra(PAYMENT_ID, payment.id)

      val approveIntent = PendingIntent.getBroadcast(
        context,
        payment.id.toInt(),
        intent.also { it.action = CompanyFcmNotificationAction.Approve.name },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

      val verifyIntent = PendingIntent.getBroadcast(
        context,
        payment.id.toInt(),
        intent.also { it.action = CompanyFcmNotificationAction.Verify.name },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
      )

      NotificationBuilder(context)
        .show(
          model = toNotifModel,
          notificationId = payment.id,
          actions = arrayOf(
            NotificationCompat.Action(R.drawable.ic_check_circle, "Approve", approveIntent),
            NotificationCompat.Action(R.drawable.ic_eye, "View", verifyIntent),
          ),
        )
    }
  }

  companion object {
    const val PAYMENT_ID = "paymentId"
    const val GOTO_VERIFY = "GOTO_VERIFY"
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
