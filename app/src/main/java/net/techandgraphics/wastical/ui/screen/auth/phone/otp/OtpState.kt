package net.techandgraphics.wastical.ui.screen.auth.phone.otp

import net.techandgraphics.wastical.domain.model.account.AccountUiModel

sealed interface OtpState {
  data object Loading : OtpState
  data class Success(
    val phone: String,
    val isRunning: Boolean = false,
    val account: AccountUiModel,
    val timeLeft: Long = 5 * 60 * 1000L,
  ) :
    OtpState
}
