package net.techandgraphics.wastemanagement.domain.model.relations

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel

data class PaymentMethodWithGatewayUiModel(
  val method: PaymentMethodUiModel,
  val gateway: PaymentGatewayUiModel,
)
