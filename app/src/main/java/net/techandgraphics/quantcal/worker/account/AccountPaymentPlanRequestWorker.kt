package net.techandgraphics.quantcal.worker.account

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.toAccountPaymentPlanRequest

@HiltWorker class AccountPaymentPlanRequestWorker @AssistedInject constructor(
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
