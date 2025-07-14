package net.techandgraphics.quantcal.domain.model.relations

import net.techandgraphics.quantcal.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel

data class PaymentMethodWithGatewayAndPlanUiModel(
  val method: PaymentMethodUiModel,
  val gateway: PaymentGatewayUiModel,
  val plan: PaymentPlanUiModel,
)
