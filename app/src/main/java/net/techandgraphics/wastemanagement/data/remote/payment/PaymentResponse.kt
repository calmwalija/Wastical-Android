package net.techandgraphics.wastemanagement.data.remote.payment

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
  val id: Long,
  @SerializedName("screenshot_url") val screenshotUrl: String,
  @SerializedName("app_trans_id") val appTransId: String,
  @SerializedName("number_of_months") val numberOfMonths: Int,
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("payment_status") val paymentStatus: PaymentStatus,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long?,
)
