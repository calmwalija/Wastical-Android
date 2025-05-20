package net.techandgraphics.wastemanagement.data.remote.payment.pay

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
  val id: Long,
  @SerializedName("screenshot_text") val screenshotText: String,
  @SerializedName("number_of_months") val numberOfMonths: Int,
  @SerializedName("transaction_id") val transactionId: String,
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("payment_status") val paymentStatus: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long?,
)
