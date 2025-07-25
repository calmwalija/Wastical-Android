package net.techandgraphics.wcompanion.ui.screen.otp

import net.techandgraphics.wcompanion.domain.model.AccountWithOtpUiModel

sealed interface OtpState {
  data object Loading : OtpState
  data class Success(
    val accountWithOtps: List<AccountWithOtpUiModel> = listOf(),
  ) : OtpState
}
