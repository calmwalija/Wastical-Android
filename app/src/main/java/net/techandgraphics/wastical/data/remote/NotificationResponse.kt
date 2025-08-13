package net.techandgraphics.wastical.data.remote

import com.google.gson.annotations.SerializedName

data class NotificationResponse(
  val id: Long,
  @SerializedName("uuid") val uuid: String,
  @SerializedName("is_read") val isRead: Boolean,
  @SerializedName("recipient_id") val recipientId: Long?,
  @SerializedName("recipient_role") val recipientRole: String,
  @SerializedName("title") val title: String,
  @SerializedName("body") val body: String,
  @SerializedName("notification_type") val type: String,
  @SerializedName("metadata") val metadata: String?,
  @SerializedName("delivered_at") val deliveredAt: Long?,
  @SerializedName("payment_id") val paymentId: Long?,
  @SerializedName("sender_id") val senderId: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
