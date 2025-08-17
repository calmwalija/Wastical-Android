package net.techandgraphics.wastical.data.local.database.notification.request

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import java.time.ZonedDateTime
import java.util.UUID

@Entity(
  tableName = "notification_request",
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
data class NotificationRequestEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  @ColumnInfo("title") val title: String,
  @ColumnInfo("body") val body: String,
  @ColumnInfo("sender_id") val senderId: Long,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("topic") val topic: String? = null,
  @ColumnInfo("notification_type") val type: String,
  @ColumnInfo("reference") val reference: String,
  @ColumnInfo("recipient_role") val recipientRole: String,
  @ColumnInfo("payment_id") val paymentId: Long? = null,
  @ColumnInfo("is_read") val isRead: Boolean = false,
  @ColumnInfo("recipient_id") val recipientId: Long? = null,
  @ColumnInfo("metadata") val metadata: String? = null,
  @ColumnInfo("delivered_at") val deliveredAt: Long? = null,
  @ColumnInfo("uuid") val uuid: String = UUID.randomUUID().toString(),
  @ColumnInfo("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @ColumnInfo("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
)
