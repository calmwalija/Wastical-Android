package net.techandgraphics.wastical.domain.model.relations

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentRequestUiModel

data class PaymentRequestWithAccountUiModel(
  val payment: PaymentRequestUiModel,
  val account: AccountUiModel,
  val fee: Int,
)
