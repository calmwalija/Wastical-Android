package net.techandgraphics.quantcal.worker

import android.accounts.AccountManager
import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.worker.client.payment.ClientPaymentRequestWorker
import net.techandgraphics.quantcal.worker.client.payment.fcm.ClientFetchLatestPaymentWorker
import net.techandgraphics.quantcal.worker.company.account.CompanyAccountDemographicRequestWorker
import net.techandgraphics.quantcal.worker.company.account.CompanyAccountPaymentPlanRequestWorker
import net.techandgraphics.quantcal.worker.company.payment.CompanyPaymentRequestWorker
import net.techandgraphics.quantcal.worker.company.payment.CompanyPaymentWorker
import net.techandgraphics.quantcal.worker.company.payment.fcm.CompanyFetchLatestPaymentWorker
import javax.inject.Inject

class WorkerFactory @Inject constructor(
  private val appDatabase: AppDatabase,
  private val paymentApi: PaymentApi,
  private val accountApi: AccountApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
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

    CompanyPaymentRequestWorker::class.java.name ->
      CompanyPaymentRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
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

    ClientPaymentRequestWorker::class.java.name ->
      ClientPaymentRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
      )

    AccountFcmTokenWorker::class.java.name ->
      AccountFcmTokenWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        accountApi = accountApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
      )

    ClientFetchLatestPaymentWorker::class.java.name ->
      ClientFetchLatestPaymentWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
      )

    CompanyFetchLatestPaymentWorker::class.java.name ->
      CompanyFetchLatestPaymentWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
      )

    else -> null
  }
}
