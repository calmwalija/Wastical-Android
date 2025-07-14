package net.techandgraphics.quantcal.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.worker.company.account.CompanyAccountDemographicRequestWorker
import net.techandgraphics.quantcal.worker.company.account.CompanyAccountPaymentPlanRequestWorker
import net.techandgraphics.quantcal.worker.company.account.CompanyAccountRequestWorker
import net.techandgraphics.quantcal.worker.company.payment.CompanyPaymentWorker
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
    CompanyPaymentWorker::class.java.name ->
      CompanyPaymentWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
      )

    CompanyAccountRequestWorker::class.java.name ->
      CompanyAccountRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
      )

    CompanyAccountDemographicRequestWorker::class.java.name ->
      CompanyAccountDemographicRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
      )

    CompanyAccountPaymentPlanRequestWorker::class.java.name ->
      CompanyAccountPaymentPlanRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
      )

    else -> null
  }
}
