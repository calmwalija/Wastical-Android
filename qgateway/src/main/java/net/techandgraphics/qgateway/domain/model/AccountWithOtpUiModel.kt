package net.techandgraphics.qgateway.domain.model

data class AccountWithOtpUiModel(
  val account: AccountUiModel,
  val otp: OtpUiModel,
)
