package net.techandgraphics.wastical.worker.company.payment

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
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.data.remote.toPaymentRequest
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyPaymentRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      val requests = database.paymentRequestDao.query()
      for (paymentRequest in requests) {
        try {
          processPaymentRequest(paymentRequest)
        } catch (e: Exception) {
          e.printStackTrace()
          // Optionally skip this one, continue to others
        }
      }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend fun processPaymentRequest(paymentRequest: PaymentRequestEntity) {
    val payment = paymentRequest.toPaymentRequest()
    val newValue = when (HttpOperation.valueOf(payment.httpOperation)) {
      HttpOperation.Post -> paymentApi.pay(payment)
      else -> paymentApi.put(paymentRequest.id, payment)
    }

    database.withTransaction {
      newValue.payments?.forEach { payment ->
        database.paymentDao.upsert(payment.toPaymentEntity())
        newValue.paymentMonthsCovered
          ?.map { it.toPaymentMonthCoveredEntity() }
          ?.forEach { database.paymentMonthCoveredDao.upsert(it) }

        newValue.notifications
          ?.map { it.toNotificationEntity() }
          ?.forEach { database.notificationDao.upsert(it) }

        database.paymentRequestDao.delete(paymentRequest)
      }
    }

    showNotification()
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
          style = NotificationCompat.BigTextStyle().bigText(notification.body),
          contentIntent = null,
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

private const val WORKER_UUID = "d6f2d1a0-9ffa-4476-bf3e-9ec8c0c6e1b0"

fun Context.scheduleCompanyPaymentRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyPaymentRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.KEEP,
      request = workRequest,
    )
}
