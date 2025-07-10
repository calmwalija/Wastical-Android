package net.techandgraphics.quantcal.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.worker.account.AccountDemographicRequestWorker
import net.techandgraphics.quantcal.worker.account.AccountPaymentPlanRequestWorker
import net.techandgraphics.quantcal.worker.account.AccountRequestWorker
import net.techandgraphics.quantcal.worker.payment.PaymentWorker
import javax.inject.Inject

class WorkerFactory @Inject constructor(
  private val appDatabase: AppDatabase,
  private val paymentApi: PaymentApi,
  private val accountApi: AccountApi,
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ) = when (workerClassName) {
    PaymentWorker::class.java.name ->
      PaymentWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
      )

    AccountRequestWorker::class.java.name ->
      AccountRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
      )

    AccountDemographicRequestWorker::class.java.name ->
      AccountDemographicRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
      )

    AccountPaymentPlanRequestWorker::class.java.name ->
      AccountPaymentPlanRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
      )

    else -> null
  }
}
