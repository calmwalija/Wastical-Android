package net.techandgraphics.quantcal.ui.screen.auth.phone.otp

sealed interface OtpState {
  data object Loading : OtpState
  data class Success(val phone: String) : OtpState
}
