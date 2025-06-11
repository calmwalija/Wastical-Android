package net.techandgraphics.wastemanagement.data.remote.payment.pay.month.covered

import com.google.gson.annotations.SerializedName

data class PaymentMonthCoveredRequest(
  val month: Int,
  val year: Int,
  @SerializedName("payment_id") val paymentId: Long,
  @SerializedName("account_id") val accountId: Long,
)
