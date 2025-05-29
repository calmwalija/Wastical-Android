package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import javax.inject.Inject

class AppWorkerFactory @Inject constructor(
  private val database: AppDatabase,
  private val api: PaymentApi,
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ) = PaymentRetryWorker(appContext, workerParameters, database, api)
}
