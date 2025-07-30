package net.techandgraphics.wastical.domain.model.account

data class AccountWithPaymentStatusUiModel(
  val account: AccountUiModel,
  val hasPaid: Boolean,
  val offlinePay: Boolean,
  val amount: Int,
)
