package net.techandgraphics.quantcal.domain.model.relations

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentRequestUiModel

data class PaymentRequestWithAccountUiModel(
  val payment: PaymentRequestUiModel,
  val account: AccountUiModel,
  val fee: Int,
)
