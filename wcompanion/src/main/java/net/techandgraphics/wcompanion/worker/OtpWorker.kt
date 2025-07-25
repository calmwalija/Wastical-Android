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
import net.techandgraphics.wcompanion.data.local.database.QgatewayDatabase
import net.techandgraphics.wcompanion.data.remote.SmsApi
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class OtpWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: QgatewayDatabase,
  private val smsApi: SmsApi,
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
    val epochSecond = database.optDao.getByUpdatedAtLatest()?.updatedAt ?: -1
    val response = smsApi.getLatest(epochSecond)
    database.withTransaction {
      response.otps
        ?.map { it.toOtpEntity() }
        ?.also { database.optDao.insert(it) }
      response.accounts
        ?.map { it.toAccountEntity() }
        ?.also { database.accountDao.insert(it) }
      context.scheduleSmsWorker()
    }
  }
}

private const val WORKER_UUID = "8f8d2715-f331-42e8-895b-33ca585147c9"

fun Context.scheduleOtpWorker() {
  val workRequest = OneTimeWorkRequestBuilder<OtpWorker>()
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
