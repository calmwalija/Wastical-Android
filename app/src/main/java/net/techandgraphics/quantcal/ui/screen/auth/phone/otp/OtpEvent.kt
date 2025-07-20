package net.techandgraphics.quantcal.ui.screen.auth.phone.otp

sealed interface OtpEvent {
  data class Load(val phone: String) : OtpEvent
  data class Otp(val opt: String) : OtpEvent

  sealed interface Goto : OtpEvent {
    data object Home : Goto
  }

  sealed interface Timer : OtpEvent {
    data object Start : Timer
    data object Pause : Timer
    data object Reset : Timer
    data object Failed : Timer
    data object TimedOut : Timer
  }
}
