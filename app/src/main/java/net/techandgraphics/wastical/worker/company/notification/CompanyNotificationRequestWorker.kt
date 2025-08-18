package net.techandgraphics.wastical.worker.company.notification

import android.content.Context
import androidx.core.app.NotificationCompat
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
import kotlinx.coroutines.flow.firstOrNull
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.notification.request.NotificationRequestEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.remote.notification.NotificationApi
import net.techandgraphics.wastical.data.remote.toNotificationRequest
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import java.time.ZonedDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@HiltWorker class CompanyNotificationRequestWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val notificationApi: NotificationApi,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    var onFailure = false
    return try {
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
      showNotification()
      if (onFailure) Result.retry() else Result.success()
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }

  private suspend fun onDoWork(notification: NotificationRequestEntity) {
    val request = notification.toNotificationRequest()
    val newValue = notificationApi.post(request)
    database.withTransaction {
      newValue.notifications
        ?.map { it.toNotificationEntity() }
        ?.forEach { notification ->
          database.notificationDao.upsert(notification)
          database.notificationRequestDao.deleteByUuid(notification.uuid)
        }
    }
  }

  private suspend fun showNotification() {
    database.notificationDao
      .flowOfSync(AccountRole.Company.name)
      .firstOrNull()
      ?.forEach { notification ->
        val theType = NotificationType.valueOf(notification.type)
        val toNotifModel = NotificationBuilderModel(
          type = theType,
          title = theType.description,
          body = notification.body,
          style = NotificationCompat.BigTextStyle().bigText(notification.body),
          contentIntent = null,
        )
        database.notificationDao.upsert(
          notification.copy(
            deliveredAt = ZonedDateTime.now().toEpochSecond(),
            syncStatus = 2,
          ),
        )
        NotificationBuilder(context)
          .show(
            model = toNotifModel,
            notificationId = notification.id,
          )
      }
  }
}

private const val WORKER_UUID = "c3e04a3f-e8e2-4faf-98fd-7faba1e9a270"

fun Context.scheduleCompanyNotificationRequestWorker() {
  val workRequest = OneTimeWorkRequestBuilder<CompanyNotificationRequestWorker>()
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.SECONDS)
    .setId(UUID.fromString(WORKER_UUID))
    .build()
  WorkManager.Companion
    .getInstance(this)
    .enqueueUniqueWork(
      uniqueWorkName = WORKER_UUID,
      existingWorkPolicy = ExistingWorkPolicy.KEEP,
      request = workRequest,
    )
}
