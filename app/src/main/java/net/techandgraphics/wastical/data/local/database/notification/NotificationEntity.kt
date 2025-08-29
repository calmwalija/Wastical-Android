package net.techandgraphics.wastical.data.local.database.notification

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity

@Entity(
  tableName = "notification",
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["sender_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PaymentEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("sender_id"),
    Index("payment_id"),
  ],
)
data class NotificationEntity(
  @PrimaryKey(autoGenerate = false) val id: Long,
  @ColumnInfo("uuid") val uuid: String,
  @ColumnInfo("body") val body: String,
  @ColumnInfo("title") val title: String,
  @ColumnInfo("topic") val topic: String?,
  @ColumnInfo("is_read") val isRead: Boolean,
  @ColumnInfo("reference") val reference: String,
  @ColumnInfo("recipient_id") val recipientId: Long?,
  @ColumnInfo("recipient_role") val recipientRole: String,
  @ColumnInfo("sender_id") val senderId: Long,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("payment_id") val paymentId: Long?,
  @ColumnInfo("notification_type") val type: String,
  @ColumnInfo("metadata") val metadata: String?,
  @ColumnInfo("delivered_at") val deliveredAt: Long?,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
  @ColumnInfo("sync_status") val syncStatus: Int = NotificationSyncStatus.Sync.ordinal,
)

enum class NotificationSyncStatus {
  Sync,
  Shown,
  Seen,
  Synced,
}
