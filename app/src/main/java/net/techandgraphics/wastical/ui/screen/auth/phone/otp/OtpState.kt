package net.techandgraphics.wastical.ui.screen.auth.phone.otp

sealed interface OtpState {
  data object Loading : OtpState
  data class Success(
    val phone: String,
    val isRunning: Boolean = false,
    val timeLeft: Long = 5 * 60 * 1000L,
  ) :
    OtpState
}
