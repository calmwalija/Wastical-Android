package net.techandgraphics.wastical.data.remote.payment.gateway

import com.google.gson.annotations.SerializedName

data class PaymentGatewayResponse(
  val id: Long,
  val name: String,
  val type: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
