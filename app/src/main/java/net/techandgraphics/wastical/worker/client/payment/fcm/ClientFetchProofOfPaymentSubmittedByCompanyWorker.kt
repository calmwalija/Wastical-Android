package net.techandgraphics.wastical.worker.client.payment.fcm

import android.accounts.AccountManager
import android.content.Context
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
import com.google.gson.Gson
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.defaultDateTime
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getTimeOfDay
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.notification.pendingIntent
import net.techandgraphics.wastical.theAmount
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toFullName
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.worker.client.payment.GOTO_NOTIFICATION
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientFetchProofOfPaymentSubmittedByCompanyWorker @AssistedInject constructor(
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
        val newValue = paymentApi.fetchLatestByCompany(account.id, epochSecond)
        database.withTransaction {
          newValue.payments?.map { it.toPaymentEntity() }
            ?.let { payments -> database.paymentDao.upsert(payments) }

          newValue.paymentMonthsCovered
            ?.map { it.toPaymentMonthCoveredEntity() }
            ?.also { database.paymentMonthCoveredDao.upsert(it) }

          newValue.notifications?.map { notificationResponse ->
            notificationResponse.toNotificationEntity()
          }?.forEach { notification ->
            val payment = database.paymentDao.get(notification.paymentId!!)
            val method = database.paymentMethodDao.get(payment.paymentMethodId)
            val theAmount = database.theAmount(payment).toAmount()
            val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
            val theBody =
              "Proof of payment of $theAmount has been submitted on your behalf by the company."
            val bigText =
              "Greetings ${account.toFullName()}, a new proof of payment of $theAmount " +
                "using ${gateway.name} " +
                "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()} " +
                "has been submitted and ${payment.status}. " +
                "Have a good ${getTimeOfDay()}"

            val newNotification = notification.copy(bigText = bigText, body = theBody)

            if (database.notificationDao.get(newNotification.id) == null) {
              database.notificationDao.upsert(newNotification)
            }
          }
        }
        showNotification()
      } ?: throw IllegalStateException()
  }

  private suspend fun showNotification() {
    database.notificationDao
      .flowOfSync()
      .firstOrNull()
      ?.forEach { notification ->
        val theType = NotificationType.valueOf(notification.type)
        val toNotifModel = NotificationBuilderModel(
          type = theType,
          title = theType.description,
          body = notification.body,
          style = NotificationCompat.BigTextStyle().bigText(notification.bigText),
          contentIntent = pendingIntent(context, GOTO_NOTIFICATION),
        )
        database.notificationDao.upsert(
          notification.copy(
            deliveredAt = ZonedDateTime.now().toEpochSecond(),
            syncStatus = 2,
          ),
        )
        NotificationBuilder(context)
          .show(
            model = toNotifModel,
            notificationId = notification.id,
          )
      }
  }
}

private const val WORKER_UUID = "ee5f2824-77f1-440b-9ec7-0b95e0c950dc"

fun Context.scheduleClientFetchProofOfPaymentSubmittedByCompanyWorker() {
  val workRequest = OneTimeWorkRequestBuilder<ClientFetchProofOfPaymentSubmittedByCompanyWorker>()
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
