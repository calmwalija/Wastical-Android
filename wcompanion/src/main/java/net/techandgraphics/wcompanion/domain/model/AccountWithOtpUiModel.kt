package net.techandgraphics.wcompanion.domain.model

data class AccountWithOtpUiModel(
  val account: AccountUiModel,
  val otp: OtpUiModel,
)
