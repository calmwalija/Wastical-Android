package net.techandgraphics.wastical.ui.screen.auth.phone.otp

sealed interface OtpChannel {
  data object Success : OtpChannel
  data object Verify : OtpChannel
  data class Error(val error: Throwable) : OtpChannel
}
