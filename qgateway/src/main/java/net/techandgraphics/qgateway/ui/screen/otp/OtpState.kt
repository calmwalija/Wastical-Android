package net.techandgraphics.qgateway.ui.screen.otp

import net.techandgraphics.qgateway.domain.model.AccountWithOtpUiModel

sealed interface OtpState {
  data object Loading : OtpState
  data class Success(
    val accountWithOtps: List<AccountWithOtpUiModel> = listOf(),
  ) : OtpState
}
