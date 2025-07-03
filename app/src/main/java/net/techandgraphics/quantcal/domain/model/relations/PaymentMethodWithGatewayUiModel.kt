package net.techandgraphics.quantcal.domain.model.relations

import net.techandgraphics.quantcal.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMethodUiModel

data class PaymentMethodWithGatewayUiModel(
  val method: PaymentMethodUiModel,
  val gateway: PaymentGatewayUiModel,
)
