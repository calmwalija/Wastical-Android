package net.techandgraphics.wastical.worker.client.payment

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
import net.techandgraphics.wastical.toZonedDateTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

private const val WORK_NAME = "client_payment_due_reminder_work"

@HiltWorker
class ClientPaymentDueReminderWorker @AssistedInject constructor(
  @Assisted val context: Context,
  @Assisted params: WorkerParameters,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : CoroutineWorker(context, params) {

  override suspend fun doWork(): Result {
    val internal = authenticatorHelper.getAccount(accountManager) ?: return Result.retry()
    val account = database.accountDao.get(internal.id)
    val company = database.companyDao.query().firstOrNull() ?: return Result.success()
    val lastCovered = database.paymentMonthCoveredDao.getLastByAccount(account.id)
    val aging = database.paymentIndicatorDao.qAgingRawByAccountId(account.id)

    val monthsOutstanding = run {
      if (aging != null) {
        val today: ZonedDateTime = ZonedDateTime.now()
        val createdZdt = aging.createdAt.toZonedDateTime()
        val startYm = YearMonth.of(createdZdt.year, createdZdt.month)
        val billingYm = if (today.dayOfMonth >= company.billingDate) {
          YearMonth.of(today.year, today.month)
        } else {
          YearMonth.of(today.year, today.month).minusMonths(1)
        }
        val targetYm = billingYm.plusMonths(1)
        val lastCoveredYm = lastCovered?.let { YearMonth.of(it.year, it.month) }
        val firstDueYm = lastCoveredYm?.plusMonths(1) ?: startYm.plusMonths(1)
        if (firstDueYm.isAfter(targetYm)) {
          0
        } else {
          (
            ChronoUnit.MONTHS.between(firstDueYm.atDay(1), targetYm.atDay(1)).toInt() + 1
            ).coerceAtLeast(0)
        }
      } else {
        0
      }
    }

    return if (monthsOutstanding > 0) {
      val model = NotificationBuilderModel(
        type = NotificationType.PAYMENT_DUE_REMINDER,
        title = NotificationType.PAYMENT_DUE_REMINDER.description,
        body = "You have $monthsOutstanding month(s) outstanding. Please settle your balance.",
        style = NotificationCompat.BigTextStyle()
          .bigText("You have $monthsOutstanding month(s) outstanding. Please settle your balance."),
        contentIntent = pendingIntent(context, gotoToRoute = "client/home"),
      )
      NotificationBuilder(context).show(
        model = model,
        notificationId = 1001L,
      )
      Result.success()
    } else {
      Result.success()
    }
  }
}

fun Context.scheduleClientPaymentDueReminderWorker() {
  val request = PeriodicWorkRequestBuilder<ClientPaymentDueReminderWorker>(1, TimeUnit.DAYS)
    .setConstraints(Constraints(requiredNetworkType = NetworkType.CONNECTED))
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
    .build()
  WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    WORK_NAME,
    ExistingPeriodicWorkPolicy.UPDATE,
    request,
  )
}

fun Context.cancelClientPaymentDueReminderWorker() {
  WorkManager.getInstance(this).cancelUniqueWork(WORK_NAME)
}
