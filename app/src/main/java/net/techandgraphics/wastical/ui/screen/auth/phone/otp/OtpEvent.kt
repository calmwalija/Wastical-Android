package net.techandgraphics.wastical.ui.screen.auth.phone.otp

sealed interface OtpEvent {
  data class Load(val phone: String) : OtpEvent
  data class Otp(val opt: String) : OtpEvent

  sealed interface Goto : OtpEvent {
    data object Home : Goto
  }
}
