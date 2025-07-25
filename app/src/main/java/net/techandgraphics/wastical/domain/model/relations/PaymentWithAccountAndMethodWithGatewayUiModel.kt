package net.techandgraphics.wastical.domain.model.relations

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

data class PaymentWithAccountAndMethodWithGatewayUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
  val method: PaymentMethodUiModel,
  val gateway: PaymentGatewayUiModel,
  val plan: PaymentPlanUiModel,
  val coveredSize: Int,
)
