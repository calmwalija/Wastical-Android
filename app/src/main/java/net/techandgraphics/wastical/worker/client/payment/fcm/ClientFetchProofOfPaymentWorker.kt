package net.techandgraphics.wastical.worker.client.payment.fcm

import android.accounts.AccountManager
import android.content.Context
import android.util.Log
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
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getTimeOfDay
import net.techandgraphics.wastical.notification.pendingIntent
import net.techandgraphics.wastical.theAmount
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.worker.client.payment.GOTO_NOTIFICATION
import net.techandgraphics.wastical.worker.workerShowNotification
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientFetchProofOfPaymentWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val gson: Gson,
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
      ?.let { account ->
        val epochSecond = database.paymentDao.getByUpdatedAtLatest()?.updatedAt ?: -1
        val newValue = paymentApi.fetchLatest(account.id, epochSecond)

        val newPayments =
          newValue.payments?.map { it.toPaymentEntity() } ?: throw IllegalStateException()
        database.withTransaction {
          database.paymentDao.upsert(newPayments)

          newValue.paymentMonthsCovered
            ?.map { it.toPaymentMonthCoveredEntity() }
            ?.also {
              database.paymentMonthCoveredDao.upsert(it)
            }

          newValue.notifications?.map { notificationResponse ->
            notificationResponse.toNotificationEntity()
          }?.forEach { notification ->
            val payment = database.paymentDao.get(notification.paymentId!!)
            val method = database.paymentMethodDao.get(payment.paymentMethodId)
            val theAmount = database.theAmount(payment).toAmount()
            val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
            val theBody = "Your proof of payment of $theAmount has been ${payment.status}."
            val bigText =
              "Howdy ${account.toFullName()}, your proof of payment of $theAmount " +
                "using ${gateway.name} " +
                "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()} " +
                "you submitted for verification has been ${payment.status}. " +
                "Thank you for your patience. " +
                "Have a good ${getTimeOfDay()}"
            val newNotification = notification.copy(title = bigText, body = theBody)

            if (database.notificationDao.get(newNotification.id) == null) {
              Log.e("TAG", "invoke:newNotification  $newNotification")
              database.notificationDao.upsert(newNotification)
            }
          }
        }

        database.workerShowNotification(
          context,
          account,
          pendingIntent = pendingIntent(context, GOTO_NOTIFICATION),
        )
      } ?: throw IllegalStateException()
  }
}

private const val WORKER_UUID = "085afec2-116d-453e-9a4b-e06ac8e57d45"

fun Context.scheduleClientFetchProofOfPaymentWorker() {
  val workRequest = OneTimeWorkRequestBuilder<ClientFetchProofOfPaymentWorker>()
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
