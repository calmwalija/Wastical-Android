package net.techandgraphics.wastical.ui.screen.auth.phone.verify

data class VerifyPhoneState(
  val contact: String = "",
  val jwtValid: Boolean = false,
)
