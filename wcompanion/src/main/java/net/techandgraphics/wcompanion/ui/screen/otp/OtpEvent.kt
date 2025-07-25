package net.techandgraphics.wcompanion.ui.screen.otp

import net.techandgraphics.wcompanion.domain.model.OtpUiModel

sealed interface OtpEvent {
  data object Load : OtpEvent
  data class Resend(val otpUiModel: OtpUiModel) : OtpEvent
}
