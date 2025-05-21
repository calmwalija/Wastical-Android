package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentRepository
import net.techandgraphics.wastemanagement.data.remote.toPaymentRequest
import net.techandgraphics.wastemanagement.getUCropFile

@HiltWorker class PaymentRetryWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val repository: PaymentRepository,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    return try {
      database.paymentDao.queryRetry().onEach { paymentEntity ->
        val file = context.getUCropFile(paymentEntity.id)
        val request = paymentEntity.toPaymentRequest()
        val newValue = repository.onPay(file, request)
        database.paymentDao.delete(paymentEntity)
        database.paymentDao.upsert(newValue.toPaymentEntity())
        file.delete()
      }
      Result.success()
    } catch (_: Exception) {
      Result.retry()
    }
  }
}
