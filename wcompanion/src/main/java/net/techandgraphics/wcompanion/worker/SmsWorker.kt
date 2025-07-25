package net.techandgraphics.wcompanion.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.room.withTransaction
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.techandgraphics.wcompanion.data.local.database.QgatewayDatabase
import net.techandgraphics.wcompanion.helpers.SmsHelper
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class SmsWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: QgatewayDatabase,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      database.withTransaction { invoke() }
      Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend operator fun invoke() {
    database.optDao.qNotSent()
      .forEach { otp ->
        SmsHelper.send(
          context = context,
          contact = otp.contact,
          message = "<#> Your OTP code is ${otp.otp}\nVHrSSATzNbN",
        ) { event ->
          when (event) {
            SmsHelper.SentStatus.Success -> {
              CoroutineScope(Dispatchers.IO + Job()).launch {
                database.optDao.update(
                  otp.copy(
                    sent = true,
                    sentAt = ZonedDateTime.now().toEpochSecond(),
                  ),
                )
              }
            }

            else -> throw IllegalStateException(event.name)
          }
        }
      }
  }
}

private const val WORKER_UUID = "50d1bf4f-1ad1-41da-8b3c-5629906a5bb5"

fun Context.scheduleSmsWorker() {
  val workRequest = OneTimeWorkRequestBuilder<SmsWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
