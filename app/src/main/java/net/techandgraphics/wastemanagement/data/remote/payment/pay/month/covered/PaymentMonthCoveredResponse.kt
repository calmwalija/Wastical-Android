package net.techandgraphics.wastemanagement.data.remote.payment.pay.month.covered

import com.google.gson.annotations.SerializedName

data class PaymentMonthCoveredResponse(
  val id: Long,
  val month: Int,
  val year: Int,
  @SerializedName("payment_id") val paymentId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
