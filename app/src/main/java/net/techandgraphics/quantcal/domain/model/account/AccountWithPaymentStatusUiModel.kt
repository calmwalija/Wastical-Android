package net.techandgraphics.quantcal.domain.model.account

data class AccountWithPaymentStatusUiModel(
  val account: AccountUiModel,
  val hasPaid: Boolean,
  val amount: Int,
)
