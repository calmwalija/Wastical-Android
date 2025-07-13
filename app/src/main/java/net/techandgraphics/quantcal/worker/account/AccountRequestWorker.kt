package net.techandgraphics.quantcal.worker.account

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toAccountContactEntity
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
import net.techandgraphics.quantcal.data.remote.toAccountRequest
import java.util.concurrent.TimeUnit

@HiltWorker class AccountRequestWorker @AssistedInject constructor(
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
          HttpOperation.Create -> {
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

              val oldAccount = database.accountDao.get(accountRequestEntity.id)
              database.accountDao.delete(oldAccount)
              database.accountRequestDao.delete(accountRequestEntity)
            }
          }

          HttpOperation.Edit -> {
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

fun Context.scheduleAccountRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<AccountRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .build()
  WorkManager.Companion.getInstance(this).enqueue(workRequest)
}
