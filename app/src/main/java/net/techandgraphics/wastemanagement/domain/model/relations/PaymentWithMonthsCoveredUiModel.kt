package net.techandgraphics.wastemanagement.domain.model.relations

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

data class PaymentWithMonthsCoveredUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
  val covered: List<PaymentMonthCoveredUiModel>,
)
