package net.techandgraphics.wastemanagement.data.remote.payment

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
  @SerializedName("trans_ref") val transRef: String,
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("number_of_months") val numberOfMonths: Int,
  @SerializedName("payment_status") val paymentStatus: PaymentStatus = PaymentStatus.Pending,
)
