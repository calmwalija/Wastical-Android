package net.techandgraphics.quantcal.domain.model.relations

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel

data class PaymentWithMonthsCoveredUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
  val covered: List<PaymentMonthCoveredUiModel>,
)
