package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastemanagement.asApproved
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.data.remote.toPaymentRequest
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.notification.NotificationBuilder
import net.techandgraphics.wastemanagement.notification.NotificationType
import net.techandgraphics.wastemanagement.notification.NotificationUiModel
import net.techandgraphics.wastemanagement.toFullName

@HiltWorker class PaymentWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val api: PaymentApi,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    return try {
      database.withTransaction {
        database.paymentRequestDao.query().onEach { paymentRequest ->
          val request = paymentRequest.toPaymentRequest().asApproved()
          val newValue = api.pay(request)
          newValue.payments?.onEach { payment ->
            database.paymentDao.upsert(payment.toPaymentEntity())
            val account = database.accountDao.get(payment.accountId).toAccountUiModel()
            newValue.paymentMonthsCovered
              ?.map { it.toPaymentMonthCoveredEntity() }
              ?.onEach { database.paymentMonthCoveredDao.insert(it) }
            database.paymentRequestDao.delete(paymentRequest)
            val notification = NotificationUiModel(
              type = NotificationType.PaymentRecorded,
              title = "Payment Recorded Successfully",
              body = "Your payment with ${payment.transactionId} for ${account.toFullName()} was successfully recorded.",
            )
            val builder = NotificationBuilder(context)
            builder.show(notification)
          }
          Result.success()
        }
      }
      Result.retry()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}
