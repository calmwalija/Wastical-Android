package net.techandgraphics.qgateway.ui.screen.otp

import net.techandgraphics.qgateway.domain.model.OtpUiModel

sealed interface OtpEvent {
  data object Load : OtpEvent
  data class Resend(val otpUiModel: OtpUiModel) : OtpEvent
}
