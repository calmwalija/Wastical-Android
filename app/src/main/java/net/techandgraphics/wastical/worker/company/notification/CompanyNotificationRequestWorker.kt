package net.techandgraphics.wastical.worker.company.notification

import android.accounts.AccountManager
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
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.notification.request.NotificationRequestEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.remote.notification.NotificationApi
import net.techandgraphics.wastical.data.remote.toNotificationRequest
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.worker.workerShowNotification
import java.net.HttpURLConnection
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyNotificationRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val notificationApi: NotificationApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    var onFailure = false
    return authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        try {
          database
            .notificationRequestDao
            .query()
            .forEach { notification ->
              runCatching { onDoWork(notification) }
                .onFailure {
                  it.printStackTrace()
                  onFailure = true
                }
            }
          database.workerShowNotification(
            context,
            account,
          )
          if (onFailure) Result.retry() else Result.success()
        } catch (e: Exception) {
          e.printStackTrace()
          Result.retry()
        }
      } ?: Result.retry()
  }

  private suspend fun onDoWork(notification: NotificationRequestEntity) {
    val request = notification.toNotificationRequest()
    val newValue = notificationApi.post(request)
    database.withTransaction {
      when (newValue.code()) {
        HttpURLConnection.HTTP_OK,
        HttpURLConnection.HTTP_CONFLICT,
        -> {
          newValue.body()?.let { serverResponse ->
            serverResponse.notifications
              ?.map { it.toNotificationEntity() }
              ?.forEach { notification ->
                database.notificationDao.upsert(notification)
                database.notificationRequestDao.deleteByUuid(notification.uuid)
              }
          }
        }

        else -> throw IllegalStateException("HTTP_ERROR_CODE : ${newValue.code()}")
      }
    }
  }
}

private const val WORKER_UUID = "c3e04a3f-e8e2-4faf-98fd-7faba1e9a270"

fun Context.scheduleCompanyNotificationRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyNotificationRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 1, TimeUnit.MINUTES)
    .setInitialDelay(10, TimeUnit.SECONDS)
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
