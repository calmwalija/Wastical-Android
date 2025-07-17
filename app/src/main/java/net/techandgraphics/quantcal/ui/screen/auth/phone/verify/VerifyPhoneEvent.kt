package net.techandgraphics.quantcal.ui.screen.auth.phone.verify

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel

sealed interface VerifyPhoneEvent {

  sealed interface Input : VerifyPhoneEvent {
    data class Phone(val value: String) : Input
  }

  sealed interface Button : VerifyPhoneEvent {
    data object Verify : Button
  }

  sealed interface Goto : VerifyPhoneEvent {
    data class Otp(val phone: String) : Goto
    data class Home(val account: AccountUiModel) : Goto
  }
}
