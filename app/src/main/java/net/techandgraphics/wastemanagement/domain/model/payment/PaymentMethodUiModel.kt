package net.techandgraphics.wastemanagement.domain.model.payment

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType

data class PaymentMethodUiModel(
  val id: Long,
  val name: String,
  val type: PaymentType,
  val account: String,
  val paymentPlanId: Long,
  val paymentGatewayId: Long,
  val createdAt: Long,
  val updatedAt: Long?,
  val isSelected: Boolean,
)
