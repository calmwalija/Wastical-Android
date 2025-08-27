package net.techandgraphics.wastical.worker

import android.accounts.AccountManager
import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.google.gson.Gson
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.data.remote.LastUpdatedApi
import net.techandgraphics.wastical.data.remote.account.AccountApi
import net.techandgraphics.wastical.data.remote.notification.NotificationApi
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.worker.client.notification.ClientNotificationWorker
import net.techandgraphics.wastical.worker.client.payment.ClientPaymentDueReminderWorker
import net.techandgraphics.wastical.worker.client.payment.ClientPaymentRequestWorker
import net.techandgraphics.wastical.worker.client.payment.fcm.ClientFetchProofOfPaymentSubmittedByCompanyWorker
import net.techandgraphics.wastical.worker.client.payment.fcm.ClientFetchProofOfPaymentWorker
import net.techandgraphics.wastical.worker.company.account.CompanyAccountDemographicRequestWorker
import net.techandgraphics.wastical.worker.company.account.CompanyAccountPaymentPlanRequestWorker
import net.techandgraphics.wastical.worker.company.account.CompanyAccountRequestWorker
import net.techandgraphics.wastical.worker.company.notification.CompanyNotificationRequestWorker
import net.techandgraphics.wastical.worker.company.payment.CompanyPaymentRequestWorker
import net.techandgraphics.wastical.worker.company.payment.CompanyPaymentWorker
import net.techandgraphics.wastical.worker.company.payment.fcm.CompanyFetchLatestPaymentWorker
import javax.inject.Inject

class WorkerFactory @Inject constructor(
  private val appDatabase: AppDatabase,
  private val paymentApi: PaymentApi,
  private val accountApi: AccountApi,
  private val notificationApi: NotificationApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val accountSessionRepository: AccountSessionRepository,
  private val lastUpdatedApi: LastUpdatedApi,
  private val preferences: Preferences,
  private val gson: Gson,
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

    CompanyAccountRequestWorker::class.java.name ->
      CompanyAccountRequestWorker(
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
        gson = gson,
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

    AccountSessionWorker::class.java.name ->
      AccountSessionWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        authenticatorHelper = authenticatorHelper,
        repository = accountSessionRepository,
        accountManager = accountManager,
      )

    ClientFetchProofOfPaymentWorker::class.java.name ->
      ClientFetchProofOfPaymentWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
        gson = gson,
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

    ClientFetchProofOfPaymentSubmittedByCompanyWorker::class.java.name ->
      ClientFetchProofOfPaymentSubmittedByCompanyWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        paymentApi = paymentApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
        gson = gson,
      )

    ClientPaymentDueReminderWorker::class.java.name ->
      ClientPaymentDueReminderWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
      )

    AccountLastUpdatedWorker::class.java.name ->
      AccountLastUpdatedWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        lastUpdatedApi = lastUpdatedApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
        accountSessionRepository = accountSessionRepository,
        preferences = preferences,
      )

    CompanyNotificationRequestWorker::class.java.name ->
      CompanyNotificationRequestWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        notificationApi = notificationApi,
      )

    ClientNotificationWorker::class.java.name ->
      ClientNotificationWorker(
        context = appContext,
        params = workerParameters,
        database = appDatabase,
        notificationApi = notificationApi,
        authenticatorHelper = authenticatorHelper,
        accountManager = accountManager,
      )

    else -> null
  }
}
