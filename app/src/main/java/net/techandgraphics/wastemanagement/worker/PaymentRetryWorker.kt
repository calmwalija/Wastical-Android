package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.data.remote.toPaymentRequest
import net.techandgraphics.wastemanagement.getUCropFile
import net.techandgraphics.wastemanagement.notification.NotificationBuilder
import net.techandgraphics.wastemanagement.notification.NotificationType
import net.techandgraphics.wastemanagement.notification.NotificationUiModel

@HiltWorker class PaymentRetryWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val api: PaymentApi,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    return try {
      database.paymentDao.qPaymentByStatus().onEach { paymentEntity ->
        val file = context.getUCropFile(paymentEntity.id)
        val request = paymentEntity.toPaymentRequest()
        val newValue = api.pay(file, request)
        database.paymentDao.delete(paymentEntity)
        database.paymentDao.upsert(newValue.toPaymentEntity())
        file.delete()
        val notification = NotificationUiModel(
          type = NotificationType.PaymentVerification,
          title = "Payment Sent for Verification",
          body = "Your payment with ${newValue.transactionId} has been sent for verification.",
          style = NotificationCompat.BigTextStyle().bigText(
            "Your payment with ${newValue.transactionId} has been successfully sent for verification. " +
              "Please be patient while we verify your payment transaction, " +
              "you will be a notified when the verification is complete",
          ),
        )
        val builder = NotificationBuilder(context)
        builder.show(notification)
      }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}
