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
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.data.remote.toPaymentRequest
import net.techandgraphics.quantcal.defaultDateTime
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.notification.NotificationBuilder
import net.techandgraphics.quantcal.notification.NotificationType
import net.techandgraphics.quantcal.notification.NotificationUiModel
import net.techandgraphics.quantcal.toAmount
import net.techandgraphics.quantcal.toFullName
import net.techandgraphics.quantcal.toZonedDateTime
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
      database.withTransaction { invoke() }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend operator fun invoke() {
    database.paymentRequestDao.query().onEach { paymentRequest ->
      val payment = paymentRequest.toPaymentRequest()
      val newValue = when (HttpOperation.valueOf(payment.httpOperation)) {
        HttpOperation.Post -> paymentApi.pay(payment)
        else -> paymentApi.put(paymentRequest.id, payment)
      }
      newValue.payments?.onEach { payment ->
        database.paymentDao.upsert(payment.toPaymentEntity())
        newValue.paymentMonthsCovered
          ?.map { it.toPaymentMonthCoveredEntity() }
          ?.onEach { database.paymentMonthCoveredDao.insert(it) }
        val account = database.accountDao.get(payment.accountId).toAccountUiModel()
        val method = database.paymentMethodDao.get(payment.paymentMethodId)
        val plan = database.paymentPlanDao.get(method.paymentPlanId)
        val gateway = database.paymentGatewayDao.get(method.paymentGatewayId)
        val months = database.paymentMonthCoveredDao.getByPaymentId(payment.id)
        val theAmount = months.size.times(plan.fee).toAmount()
        database.paymentRequestDao.delete(paymentRequest)
        val notification = NotificationUiModel(
          type = NotificationType.PaymentRecorded,
          title = "Payment Recorded",
          body = "Payment made for ${account.toFullName()} " +
            "of $theAmount " +
            "using ${gateway.name} " +
            "on ${payment.updatedAt.toZonedDateTime().defaultDateTime()} " +
            "has been recorded",
        )
        val builder = NotificationBuilder(context)
        builder.show(notification, payment.id)
      }
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
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
