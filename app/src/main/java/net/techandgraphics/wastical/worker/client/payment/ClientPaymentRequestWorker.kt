package net.techandgraphics.wastical.worker.client.payment

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
import net.techandgraphics.wastical.asVerifying
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.data.remote.payment.PaymentType
import net.techandgraphics.wastical.data.remote.toPaymentRequest
import net.techandgraphics.wastical.getUCropFile
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.notification.pendingIntent
import net.techandgraphics.wastical.toAmount
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientPaymentRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
  private val gson: Gson,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      database.paymentRequestDao.query().forEach { paymentRequest ->
        processPayment(paymentRequest)
      }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend fun processPayment(paymentRequest: PaymentRequestEntity) {
    val request = paymentRequest.toPaymentRequest().asVerifying()
    val method = database.paymentMethodDao.get(request.paymentMethodId)
    val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)

    val newValue = when (PaymentType.valueOf(gateway.type)) {
      PaymentType.Cash -> paymentApi.pay(request)
      else -> {
        val jsonRequestBody = gson.toJson(request)
          .toRequestBody(contentType = "application/json; charset=utf-8".toMediaType())
        val file = context.getUCropFile(paymentRequest.createdAt)
        val fileRequestBody = file.asRequestBody("image/*".toMediaType())
        val multipartFile = MultipartBody.Part.createFormData(
          name = "file",
          filename = file.name,
          body = fileRequestBody,
        )
        paymentApi.payWithScreenshot(multipartFile, jsonRequestBody)
      }
    }

    database.withTransaction {
      newValue.payments?.onEach { payment ->
        database.paymentDao.upsert(payment.toPaymentEntity())
        newValue.paymentMonthsCovered
          ?.map { it.toPaymentMonthCoveredEntity() }
          ?.onEach { database.paymentMonthCoveredDao.upsert(it) }
        database.paymentRequestDao.delete(paymentRequest)
      }
      newValue.notifications?.map { notificationResponse ->
        notificationResponse.toNotificationEntity()
      }?.forEach { notification ->
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val theAmount = paymentRequest.months.times(plan.fee).toAmount()
        val theBody = "Your proof of payment of $theAmount has been submitted and awaits verification."
        val bigText =
          "$theBody We'll notify you once the verification is complete. Thank you for your patience."
        val newNotification = notification.copy(
          title = bigText,
          body = theBody,
        )
        database.notificationDao.insert(newNotification)
      }
    }

    database.notificationDao
      .flowOfSync()
      .firstOrNull()
      ?.forEach { notification ->
        val theType = NotificationType.valueOf(notification.type)
        val toNotifModel = NotificationBuilderModel(
          type = theType,
          title = theType.description,
          body = notification.body,
          style = NotificationCompat.BigTextStyle().bigText(notification.title),
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

const val GOTO_NOTIFICATION = "GOTO_NOTIFICATION"
const val INTENT_EXTRA_GOTO = "INTENT_EXTRA_GOTO"

private const val WORKER_UUID = "0add6fac-665b-4190-a10e-f1f5e72a09a4"

fun Context.scheduleClientPaymentRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<ClientPaymentRequestWorker>()
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
