package net.techandgraphics.wastemanagement.data.remote.payment.method

import com.google.gson.annotations.SerializedName

data class PaymentMethodResponse(
  val id: Long,
  val account: String,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
  @SerializedName("payment_gateway_id") val paymentGatewayId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
