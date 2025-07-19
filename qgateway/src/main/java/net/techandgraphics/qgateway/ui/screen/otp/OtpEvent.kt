package net.techandgraphics.qgateway.ui.screen.otp

sealed interface OtpEvent {
  data object Load : OtpEvent
}
