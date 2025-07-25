package net.techandgraphics.wastical.domain.model.payment

import net.techandgraphics.wastical.domain.model.account.AccountUiModel

data class PaymentAccountUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
)
