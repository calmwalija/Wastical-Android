package net.techandgraphics.quantcal.worker.company.account

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
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
import net.techandgraphics.quantcal.data.remote.toAccountRequest
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyAccountDemographicRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val accountApi: AccountApi,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      database.accountRequestDao
        .qByHttpOp(HttpOperation.Demographic.name).forEach { request ->
          val newAccount = accountApi.demographic(request.id, request.toAccountRequest())
          database.withTransaction {
            database.accountDao.update(newAccount.toAccountEntity())
            database.accountRequestDao.delete(request)
          }
        }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

fun Context.scheduleCompanyAccountDemographicRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyAccountDemographicRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(CompanyAccountDemographicRequestWorker::class.java.simpleName))
    .build()
  WorkManager
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = CompanyAccountDemographicRequestWorker::class.java.simpleName,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
