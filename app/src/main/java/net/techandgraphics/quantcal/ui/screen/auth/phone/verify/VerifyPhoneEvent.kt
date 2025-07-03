package net.techandgraphics.quantcal.ui.screen.auth.phone.verify

sealed interface VerifyPhoneEvent {

  sealed interface Input : VerifyPhoneEvent {
    data class Phone(val value: String) : Input
  }

  sealed interface Button : VerifyPhoneEvent {
    data object Verify : Button
  }

  sealed interface Goto : VerifyPhoneEvent {
    data class Otp(val phone: String) : Goto
  }
}
