package net.techandgraphics.wastical.worker.company.payment

import android.accounts.AccountManager
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
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.data.remote.toPaymentRequest
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.worker.workerShowNotification
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyPaymentRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val paymentApi: PaymentApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        try {
          val requests = database.paymentRequestDao.query()
          for (paymentRequest in requests) {
            try {
              processPaymentRequest(paymentRequest, account)
            } catch (e: Exception) {
              e.printStackTrace()
            }
          }
          Result.success()
        } catch (e: Exception) {
          e.printStackTrace()
          Result.retry()
        }
      } ?: Result.retry()
  }

  private suspend fun processPaymentRequest(
    paymentRequest: PaymentRequestEntity,
    account: AccountUiModel,
  ) {
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

    database.workerShowNotification(
      context,
      account,
    )
  }
}

private const val WORKER_UUID = "d6f2d1a0-9ffa-4476-bf3e-9ec8c0c6e1b0"

fun Context.scheduleCompanyPaymentRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyPaymentRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
    .setInitialDelay(10, TimeUnit.SECONDS)
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
