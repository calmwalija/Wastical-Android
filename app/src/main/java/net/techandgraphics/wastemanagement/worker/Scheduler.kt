package net.techandgraphics.wastemanagement.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

fun Context.schedulePaymentRetryWorker() {
  val workRequest = OneTimeWorkRequestBuilder<PaymentRetryWorker>()
    .setInitialDelay(3, TimeUnit.SECONDS)
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .build()
  WorkManager.getInstance(this).enqueue(workRequest)
}
