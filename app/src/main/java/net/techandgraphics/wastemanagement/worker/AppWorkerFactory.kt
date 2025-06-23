package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
  private val database: AppDatabase,
  private val api: PaymentApi,
  private val accountApi: AccountApi,
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ) = PaymentWorker(appContext, workerParameters, database, api, accountApi)
}
