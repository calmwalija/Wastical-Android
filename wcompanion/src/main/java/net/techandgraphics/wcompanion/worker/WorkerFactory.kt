package net.techandgraphics.wcompanion.worker

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import net.techandgraphics.wcompanion.data.local.database.QgatewayDatabase
import net.techandgraphics.wcompanion.data.remote.SmsApi
import javax.inject.Inject

class WorkerFactory @Inject constructor(
  private val database: QgatewayDatabase,
  private val smsApi: SmsApi,
) : WorkerFactory() {
  override fun createWorker(
    appContext: Context,
    workerClassName: String,
    workerParameters: WorkerParameters,
  ) = when (workerClassName) {
    OtpWorker::class.java.name ->
      OtpWorker(
        context = appContext,
        params = workerParameters,
        database = database,
        smsApi = smsApi,
      )

    FcmTokenWorker::class.java.name ->
      FcmTokenWorker(
        context = appContext,
        params = workerParameters,
        database = database,
        smsApi = smsApi,
      )

    SmsWorker::class.java.name ->
      SmsWorker(
        context = appContext,
        params = workerParameters,
        database = database,
      )

    else -> null
  }
}
