package net.techandgraphics.quantcal.worker.company.account

import android.content.Context
import androidx.hilt.work.HiltWorker
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
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.toAccountPaymentPlanRequest
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyAccountPaymentPlanRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val accountApi: AccountApi,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      database.accountPaymentPlanRequestDao.query()
        .onEach {
          val newPlan = accountApi.plan(it.paymentPlanId, it.toAccountPaymentPlanRequest())
            .toAccountPaymentPlanEntity()
          database.accountPaymentPlanDao.update(newPlan)
          database.accountPaymentPlanRequestDao.delete(it)
        }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

private const val WORKER_UUID = "876a8277-7586-4b15-a0f6-94df0277956c"

fun Context.scheduleCompanyAccountPaymentPlanRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyAccountPaymentPlanRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(WORKER_UUID))
    .build()
  WorkManager
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
