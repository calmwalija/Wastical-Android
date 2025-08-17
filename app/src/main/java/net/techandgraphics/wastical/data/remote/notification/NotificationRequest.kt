package net.techandgraphics.wastical.data.remote.notification

import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime
import java.util.UUID

data class NotificationRequest(
  @SerializedName("title") val title: String,
  @SerializedName("body") val body: String,
  @SerializedName("sender_id") val senderId: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("topic") val topic: String? = null,
  @SerializedName("notification_type") val type: String,
  @SerializedName("reference") val reference: String,
  @SerializedName("recipient_role") val recipientRole: String,
  @SerializedName("payment_id") val paymentId: Long? = null,
  @SerializedName("is_read") val isRead: Boolean = false,
  @SerializedName("recipient_id") val recipientId: Long? = null,
  @SerializedName("metadata") val metadata: String? = null,
  @SerializedName("delivered_at") val deliveredAt: Long? = null,
  @SerializedName("uuid") val uuid: String = UUID.randomUUID().toString(),
  @SerializedName("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  @SerializedName("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
)
