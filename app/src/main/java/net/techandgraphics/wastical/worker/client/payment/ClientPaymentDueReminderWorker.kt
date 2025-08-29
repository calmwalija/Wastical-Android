package net.techandgraphics.wastical.worker.client.payment

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
import net.techandgraphics.wastical.toAmount
import net.techandgraphics.wastical.toPluralMonth
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.worker.WorkerUuid.CLIENT_PAYMENT_DUE_REMINDER
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@HiltWorker
class ClientPaymentDueReminderWorker @AssistedInject constructor(
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

    return try {
      if (monthsOutstanding > 0) {
        val monthsDue = context.toPluralMonth(monthsOutstanding)
        val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
        val paymentPlan = database.paymentPlanDao.get(accountPlan.paymentPlanId)
        val balance = paymentPlan.fee.times(monthsOutstanding).toAmount()
        val theText =
          "You have $monthsDue balance which sums up to $balance. Please settle your balance."
        val notificationModel = NotificationBuilderModel(
          type = NotificationType.PAYMENT_DUE_REMINDER,
          title = NotificationType.PAYMENT_DUE_REMINDER.description,
          body = theText,
          style = NotificationCompat.BigTextStyle().bigText(theText),
          contentIntent = pendingIntent(context, gotoToRoute = "client/home"),
        )
        val entity = notificationModel.toNotificationEntity(account = account)
        database.notificationDao.upsert(entity)
        if (preferences.get(Preferences.CLIENT_REMINDER_PAYMENT, true)) {
          NotificationBuilder(context).show(entity.id, notificationModel)
        }
        Result.success()
      } else {
        Result.success()
      }
    } catch (e: Exception) {
      e.printStackTrace()
      Result.retry()
    }
  }
}

fun Context.scheduleClientPaymentDueReminderWorker() {
  val request = PeriodicWorkRequestBuilder<ClientPaymentDueReminderWorker>(1, TimeUnit.DAYS)
    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
    .setInitialDelay(20, TimeUnit.SECONDS)
    .build()
  WorkManager.getInstance(this).enqueueUniquePeriodicWork(
    CLIENT_PAYMENT_DUE_REMINDER,
    ExistingPeriodicWorkPolicy.UPDATE,
    request,
  )
}

fun Context.cancelClientPaymentDueReminderWorker() {
  WorkManager.getInstance(this)
    .cancelUniqueWork(CLIENT_PAYMENT_DUE_REMINDER)
}
