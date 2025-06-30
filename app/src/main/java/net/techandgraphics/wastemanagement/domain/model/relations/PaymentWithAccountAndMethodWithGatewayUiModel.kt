package net.techandgraphics.wastemanagement.domain.model.relations

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

data class PaymentWithAccountAndMethodWithGatewayUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
  val method: PaymentMethodUiModel,
  val gateway: PaymentGatewayUiModel,
  val plan: PaymentPlanUiModel,
  val coveredSize: Int,
)
