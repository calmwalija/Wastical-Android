package net.techandgraphics.wastical.ui.screen.auth.phone.otp

sealed interface OtpEvent {
  data class Load(val phone: String) : OtpEvent
  data class Otp(val opt: String) : OtpEvent
  data object NotMe : Goto

  sealed interface Goto : OtpEvent {
    data object Home : Goto
    data object Verify : Goto
  }
}
