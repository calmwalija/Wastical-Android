package net.techandgraphics.wastemanagement.domain.model.payment

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel

data class PaymentAccountUiModel(
  val payment: PaymentUiModel,
  val account: AccountUiModel,
)
