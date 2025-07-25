package net.techandgraphics.wastical.domain.model.relations

import net.techandgraphics.wastical.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel

data class PaymentMethodWithGatewayAndPlanUiModel(
  val method: PaymentMethodUiModel,
  val gateway: PaymentGatewayUiModel,
  val plan: PaymentPlanUiModel,
)
