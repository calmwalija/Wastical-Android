package net.techandgraphics.wastemanagement.data.remote.payment

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
  @SerializedName("screenshot_text") val screenshotText: String,
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("number_of_months") val numberOfMonths: Int,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("executed_by_id") val executedById: Long,
  @SerializedName("payment_status") val status: PaymentStatus = PaymentStatus.Failed,
)
