package net.techandgraphics.wastical.worker.company.account

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
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toAccountContactEntity
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.remote.account.AccountApi
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.toAccountRequest
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyAccountRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val accountApi: AccountApi,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      database.accountRequestDao.query().forEach { accountRequestEntity ->
        val account = accountRequestEntity.toAccountRequest()
        when (HttpOperation.valueOf(account.httpOperation)) {
          HttpOperation.Post -> {
            val newAccount = accountApi.create(account)
            database.withTransaction {
              newAccount.accounts
                ?.map { it.toAccountEntity() }
                ?.also { database.accountDao.insert(it) }

              newAccount.accountContacts
                ?.map { it.toAccountContactEntity() }
                ?.also { database.accountContactDao.insert(it) }

              newAccount.accountPaymentPlans
                ?.map { it.toAccountPaymentPlanEntity() }
                ?.also { database.accountPaymentPlanDao.insert(it) }

              newAccount.payments
                ?.map { it.toPaymentEntity() }
                ?.also { database.paymentDao.insert(it) }

              newAccount.paymentMonthsCovered
                ?.map { it.toPaymentMonthCoveredEntity() }
                ?.also { database.paymentMonthCoveredDao.insert(it) }

              val oldAccount = database.accountDao.get(accountRequestEntity.id)
              database.accountDao.delete(oldAccount)
              database.accountRequestDao.delete(accountRequestEntity)
            }
          }

          HttpOperation.Put -> {
            database.withTransaction {
              accountApi.put(accountRequestEntity.id, account)
                .toAccountEntity()
                .also { database.accountDao.update(it) }
              database.accountRequestDao.delete(accountRequestEntity)
            }
          }

          HttpOperation.Demographic -> Unit
        }
      }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

private const val WORKER_UUID = "6d79bbb6-9120-47f4-9483-1018edeaea38"

fun Context.scheduleCompanyAccountRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyAccountRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
    .setInitialDelay(10, TimeUnit.SECONDS)
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
