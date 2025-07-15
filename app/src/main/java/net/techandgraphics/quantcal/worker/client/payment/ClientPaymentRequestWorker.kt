package net.techandgraphics.quantcal.worker.client.payment

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
import net.techandgraphics.quantcal.asVerifying
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentType
import net.techandgraphics.quantcal.data.remote.toPaymentRequest
import net.techandgraphics.quantcal.getUCropFile
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.notification.NotificationType
import net.techandgraphics.quantcal.notification.NotificationUiModel
import net.techandgraphics.quantcal.toAmount
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientPaymentRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
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
    database.paymentRequestDao.query().onEach { paymentRequest ->
      val request = paymentRequest.toPaymentRequest().asVerifying()
      val method = database.paymentMethodDao.get(request.paymentMethodId)
      val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
      val newValue = when (PaymentType.valueOf(gateway.type)) {
        PaymentType.Cash -> paymentApi.pay(request)
        else -> {
          val jsonRequestBody = Gson().toJson(request)
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
      newValue.payments?.onEach { payment ->
        database.paymentDao.upsert(payment.toPaymentEntity())
        newValue.paymentMonthsCovered
          ?.map { it.toPaymentMonthCoveredEntity() }
          ?.onEach { database.paymentMonthCoveredDao.insert(it) }
        database.paymentRequestDao.delete(paymentRequest)
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val theAmount = paymentRequest.months.times(plan.fee).toAmount()
        val notification = NotificationUiModel(
          type = NotificationType.PaymentVerification,
          title = "Payment Sent for Verification",
          body = "Your payment of $theAmount has been sent for verification.",
          style = NotificationCompat.BigTextStyle().bigText(
            "Your payment of $theAmount has been sent for verification. " +
              "We'll notify you once the verification is complete. Thank you for your patience.",
          ),
        )
        val builder = NotificationBuilder(context)
        builder.show(notification)
      }
    }
  }
}

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
