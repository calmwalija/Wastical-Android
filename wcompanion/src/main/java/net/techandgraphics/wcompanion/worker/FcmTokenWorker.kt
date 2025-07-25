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

@HiltWorker class FcmTokenWorker @AssistedInject constructor(
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
    database.fcmTokenDao.query()
      .filterNot { it.sync.not() }
      .map { it.fcmTokenRequest(726) }
      .onEach {
        val fcmTokenResponse = smsApi.fcmToken(it)
        database.withTransaction {
          with(database.fcmTokenDao) {
            deleteAll()
            insert(fcmTokenResponse.toFcmTokenEntity())
          }
        }
      }
  }
}

private const val WORKER_UUID = "d834872c-ea09-4bee-ab2f-e5272a211c93"

fun Context.scheduleFcmTokenWorker() {
  val workRequest = OneTimeWorkRequestBuilder<FcmTokenWorker>()
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
