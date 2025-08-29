package net.techandgraphics.wastical.worker.client.notification

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
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.remote.notification.NotificationApi
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.worker.WorkerUuid.CLIENT_NOTIFICATION
import net.techandgraphics.wastical.worker.workerShowNotification
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class ClientNotificationWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val notificationApi: NotificationApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    return authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        try {
          val mills = database.notificationDao.getLastUpdatedTimestamp()
          notificationApi.latest(account.id, mills)
            .map { it.toNotificationEntity() }
            .also { database.notificationDao.insert(it) }
          database.workerShowNotification(context, account)
          Result.success()
        } catch (e: Exception) {
          e.printStackTrace()
          Result.retry()
        }
      } ?: Result.retry()
  }
}

fun Context.scheduleClientNotificationWorker() {
  val workRequest = OneTimeWorkRequestBuilder<ClientNotificationWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(CLIENT_NOTIFICATION))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = CLIENT_NOTIFICATION,
      existingWorkPolicy = ExistingWorkPolicy.KEEP,
      request = workRequest,
    )
}
