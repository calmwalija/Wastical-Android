package net.techandgraphics.wastical.domain.model

import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.notification.NotificationSyncStatus
import net.techandgraphics.wastical.notification.NotificationType

data class NotificationUiModel(
  val id: Long,
  val uuid: String,
  val body: String,
  val bigText: String,
  val isRead: Boolean,
  val recipientId: Long?,
  val recipientRole: AccountRole,
  val senderId: Long,
  val type: NotificationType,
  val metadata: String?,
  val deliveredAt: Long?,
  val createdAt: Long,
  val updatedAt: Long,
  val syncStatus: NotificationSyncStatus,
)
