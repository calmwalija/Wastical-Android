package net.techandgraphics.wastical.notification

import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.notification.NotificationEntity
import net.techandgraphics.wastical.data.local.database.notification.NotificationSyncStatus
import net.techandgraphics.wastical.getReference
import java.time.ZonedDateTime
import java.util.UUID

data class NotificationBuilderModel(
  val type: NotificationType,
  val title: String,
  val body: String,
  val style: NotificationCompat.Style? = null,
  val contentIntent: PendingIntent? = null,
  val iconRes: Int? = null,
)

fun NotificationBuilderModel.toNotificationEntity(
  account: AccountEntity,
  epochSecond: Long = ZonedDateTime.now().toEpochSecond(),
) = NotificationEntity(
  id = System.currentTimeMillis(),
  uuid = UUID.randomUUID().toString(),
  body = this.body,
  title = this.title,
  topic = account.uuid,
  isRead = false,
  reference = getReference(),
  recipientId = account.id,
  recipientRole = AccountRole.Client.name,
  senderId = account.id,
  companyId = account.companyId,
  paymentId = null,
  type = this.type.name,
  metadata = null,
  syncStatus = NotificationSyncStatus.Sync.ordinal,
  deliveredAt = epochSecond,
  createdAt = epochSecond,
  updatedAt = epochSecond,
)
