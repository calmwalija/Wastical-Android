package net.techandgraphics.wastical.worker

import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.flow.firstOrNull
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.notification.NotificationSyncStatus
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.notification.NotificationBuilder
import net.techandgraphics.wastical.notification.NotificationBuilderModel
import net.techandgraphics.wastical.notification.NotificationType
import java.time.ZonedDateTime

suspend fun AppDatabase.workerShowNotification(
  context: Context,
  account: AccountUiModel,
  pendingIntent: PendingIntent? = null,
) {
  notificationDao
    .flowOfSync(role = account.role)
    .firstOrNull()
    ?.forEachIndexed { index, notification ->
      val theType = NotificationType.valueOf(notification.type)
      val notificationBuilderModel = NotificationBuilderModel(
        type = theType,
        title = theType.description,
        body = notification.body,
        style = NotificationCompat.BigTextStyle().bigText(notification.body),
        contentIntent = pendingIntent,
      )

      val syncStatus =
        if (notification.deliveredAt == null) NotificationSyncStatus.Sync.ordinal else notification.syncStatus

      notificationDao.upsert(
        notification.copy(
          deliveredAt = ZonedDateTime.now().toEpochSecond(),
          syncStatus = syncStatus,
        ),
      )
      NotificationBuilder(context)
        .show(
          model = notificationBuilderModel,
          notificationId = notification.id,
        )

      context.scheduleNotificationRequestWorker()
    }
}
