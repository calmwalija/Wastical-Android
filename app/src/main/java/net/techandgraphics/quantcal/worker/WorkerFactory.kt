package net.techandgraphics.quantcal.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
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
  ) = AppWorker(
    context = appContext,
    params = workerParameters,
    database = appDatabase,
    paymentApi = paymentApi,
    accountApi = accountApi,
  )
}
