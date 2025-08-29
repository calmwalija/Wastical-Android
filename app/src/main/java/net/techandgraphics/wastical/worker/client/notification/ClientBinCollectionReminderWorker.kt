package net.techandgraphics.wastical.worker.client.notification

import android.accounts.AccountManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.notification.pendingIntent
import net.techandgraphics.wastical.notification.toNotificationEntity
import net.techandgraphics.wastical.worker.WorkerUuid.CLIENT_BIN_COLLECTION_REMINDER
import net.techandgraphics.wastical.worker.scheduleNotificationRequestWorker
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

@HiltWorker
class ClientBinCollectionReminderWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val preferences: Preferences,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    val internal = authenticatorHelper.getAccount(accountManager) ?: return Result.retry()
    val account = database.accountDao.get(internal.id)
    val collections = database.companyBinCollectionDao.query()

    val today: DayOfWeek = ZonedDateTime.now().dayOfWeek
    val matchesToday = collections.any { it.dayOfWeek.equals(today.name, ignoreCase = true) }

    if (matchesToday) {
      val notificationModel = NotificationBuilderModel(
        type = NotificationType.WASTE_COLLECTION_REMINDER,
        title = "Bin collection today",
        body = "Your bin will be collected today. Please put it out.",
        style = NotificationCompat.BigTextStyle()
          .bigText("Your bin will be collected today. Please put it out."),
        contentIntent = pendingIntent(context, gotoToRoute = "client/home"),
      )
      val entity = notificationModel.toNotificationEntity(account = account)
      database.notificationDao.upsert(entity)
      if (preferences.get(Preferences.CLIENT_REMINDER_BIN, true)) {
        NotificationBuilder(context).show(entity.id, notificationModel)
      }
    }
    context.scheduleNotificationRequestWorker()
    return Result.success()
  }
}

fun Context.scheduleClientBinCollectionReminderWorker() {
  val now = ZonedDateTime.now()
  val targetTime = LocalTime.of(5, 0)
  val todayAtTarget =
    now.withHour(targetTime.hour)
      .withMinute(targetTime.minute)
      .withSecond(0)
      .withNano(0)
  val nextRun = if (now.isAfter(todayAtTarget)) todayAtTarget.plusDays(1) else todayAtTarget
  val initialDelayMinutes = Duration.between(now, nextRun).toMinutes().coerceAtLeast(0)

  val request = PeriodicWorkRequestBuilder<ClientBinCollectionReminderWorker>(1, TimeUnit.DAYS)
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
    .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
    .build()
  WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    CLIENT_BIN_COLLECTION_REMINDER,
    ExistingPeriodicWorkPolicy.UPDATE,
    request,
  )
}

fun Context.cancelClientBinCollectionReminderWorker() {
  WorkManager.getInstance(this)
    .cancelUniqueWork(CLIENT_BIN_COLLECTION_REMINDER)
}
