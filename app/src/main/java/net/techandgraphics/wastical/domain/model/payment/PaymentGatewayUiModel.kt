package net.techandgraphics.wastical.domain.model.payment

data class PaymentGatewayUiModel(
  val id: Long,
  val name: String,
  val type: String,
  val createdAt: Long,
  val updatedAt: Long,
)
