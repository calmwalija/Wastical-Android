package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi

@HiltWorker class PaymentRetryWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val api: PaymentApi,
) : CoroutineWorker(context, params) {
  override suspend fun doWork(): Result {
    return Result.success()
  }
}
