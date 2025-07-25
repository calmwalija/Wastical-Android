package net.techandgraphics.wastical.domain.model.payment

data class PaymentMethodUiModel(
  val id: Long,
  val account: String,
  val paymentPlanId: Long,
  val paymentGatewayId: Long,
  val createdAt: Long,
  val updatedAt: Long,
  val isSelected: Boolean,
)
