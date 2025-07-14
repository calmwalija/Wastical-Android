package net.techandgraphics.quantcal.worker.company.payment

import android.content.Context
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
import net.techandgraphics.quantcal.asApproved
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.data.remote.toPaymentRequest
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.notification.NotificationType
import net.techandgraphics.quantcal.notification.NotificationUiModel
import net.techandgraphics.quantcal.toFullName
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyPaymentWorker @AssistedInject constructor(
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
      val request = paymentRequest.toPaymentRequest().asApproved()
      val newValue = paymentApi.pay(request)
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
    }
  }
}

fun Context.scheduleCompanyPaymentWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyPaymentWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(CompanyPaymentWorker::class.java.simpleName))
    .build()
  WorkManager
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = CompanyPaymentWorker::class.java.simpleName,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
