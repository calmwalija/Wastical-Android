package net.techandgraphics.wastical.worker.client.notification

import android.accounts.AccountManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.notification.pendingIntent
import java.time.DayOfWeek
import java.time.ZonedDateTime
import java.time.LocalTime
import java.time.Duration
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "client_bin_collection_reminder_work"

@HiltWorker
class ClientBinCollectionReminderWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    val internal = authenticatorHelper.getAccount(accountManager) ?: return Result.retry()
    val account = database.accountDao.get(internal.id)
    val collections = database.companyBinCollectionDao.query()

    val today: DayOfWeek = ZonedDateTime.now().dayOfWeek
    val matchesToday = collections.any { it.dayOfWeek.equals(today.name, ignoreCase = true) }

    if (matchesToday) {
      val model = NotificationBuilderModel(
        type = NotificationType.WASTE_COLLECTION_REMINDER,
        title = "Bin collection today",
        body = "Reminder: Your bin will be collected today. Please put it out.",
        style = NotificationCompat.BigTextStyle()
          .bigText("Reminder: Your bin will be collected today. Please put it out."),
        contentIntent = pendingIntent(context, gotoToRoute = "client/home"),
      )
      NotificationBuilder(context).show(
        model = model,
        notificationId = 2001L,
      )
    }
    return Result.success()
  }
}

fun Context.scheduleClientBinCollectionReminderWorker() {
  val now = ZonedDateTime.now()
  val targetTime = LocalTime.of(5, 0)
  val todayAtTarget = now.withHour(targetTime.hour).withMinute(targetTime.minute).withSecond(0).withNano(0)
  val nextRun = if (now.isAfter(todayAtTarget)) todayAtTarget.plusDays(1) else todayAtTarget
  val initialDelayMinutes = Duration.between(now, nextRun).toMinutes().coerceAtLeast(0)

  val request = PeriodicWorkRequestBuilder<ClientBinCollectionReminderWorker>(1, TimeUnit.DAYS)
    .setConstraints(Constraints(requiredNetworkType = NetworkType.NOT_REQUIRED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
    .setInitialDelay(initialDelayMinutes, TimeUnit.MINUTES)
    .build()
  WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    WORK_NAME,
    ExistingPeriodicWorkPolicy.UPDATE,
    request,
  )
}

fun Context.cancelClientBinCollectionReminderWorker() {
  WorkManager.getInstance(this).cancelUniqueWork(WORK_NAME)
}


