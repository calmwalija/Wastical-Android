package net.techandgraphics.wastical.worker

import android.accounts.AccountManager
import android.content.Context
import androidx.hilt.work.HiltWorker
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.remote.notification.NotificationApi
import net.techandgraphics.wastical.data.remote.toNotificationRequest
import net.techandgraphics.wastical.data.remote.toNotificationRequestEntity
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.worker.WorkerUuid.NOTIFICATION_REQUEST
import java.net.HttpURLConnection
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class NotificationRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val notificationApi: NotificationApi,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return try {
      authenticatorHelper.getAccount(accountManager)
        ?.let { account ->
          database.notificationDao
            .flowOfSync(role = account.role)
            .map { p0 -> p0.map { it.toNotificationRequestEntity() } }
            .collectLatest { notifications ->
              database.notificationRequestDao.insert(notifications)
              notifications
                .map { it.toNotificationRequest() }
                .forEach { notificationRequest ->
                  val response = notificationApi.post(notificationRequest)
                  when (response.code()) {
                    HttpURLConnection.HTTP_OK,
                    HttpURLConnection.HTTP_CONFLICT,
                    -> {
                      response.body()?.let { serverResponse ->
                        serverResponse
                          .notifications
                          ?.map { it.toNotificationEntity() }
                          ?.also { notificationRequests ->
                            database.notificationDao.upsert(notificationRequests)
                            notificationRequests
                              .map { it.uuid }
                              .forEach { uuid -> database.notificationRequestDao.deleteByUuid(uuid) }
                          }
                      }
                      Result.success()
                    }

                    else -> throw IllegalStateException("HTTP_ERROR_CODE : ${response.code()}")
                  }
                }
            }
          Result.success()
        } ?: Result.retry()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

fun Context.scheduleNotificationRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<NotificationRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
    .setInitialDelay(10, TimeUnit.SECONDS)
    .setId(UUID.fromString(NOTIFICATION_REQUEST))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = NOTIFICATION_REQUEST,
      existingWorkPolicy = ExistingWorkPolicy.REPLACE,
      request = workRequest,
    )
}
