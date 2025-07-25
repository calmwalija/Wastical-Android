package net.techandgraphics.wastical.domain.model.relations

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

data class PaymentWithMonthsCoveredUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
  val covered: List<PaymentMonthCoveredUiModel>,
)
