package net.techandgraphics.wastemanagement.data.remote.payment.pay

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
  val id: Long,
  val months: Int,
  @SerializedName("screenshot_text") val screenshotText: String,
  @SerializedName("transaction_id") val transactionId: String,
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("payment_status") val status: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("executed_by_id") val executedById: Long,
)
