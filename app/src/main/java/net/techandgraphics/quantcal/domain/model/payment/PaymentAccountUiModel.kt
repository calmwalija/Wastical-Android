package net.techandgraphics.quantcal.domain.model.payment

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel

data class PaymentAccountUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
)
