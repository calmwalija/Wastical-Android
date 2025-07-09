package net.techandgraphics.quantcal.worker.account

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toAccountContactEntity
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.toAccountRequest

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
        }
      }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}
