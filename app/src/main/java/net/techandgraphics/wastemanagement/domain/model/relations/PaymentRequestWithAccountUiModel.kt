package net.techandgraphics.wastemanagement.domain.model.relations

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentRequestUiModel

data class PaymentRequestWithAccountUiModel(
  val payment: PaymentRequestUiModel,
  val account: AccountUiModel,
  val fee: Int,
)
