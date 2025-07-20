package net.techandgraphics.quantcal.ui.screen.auth.phone.otp

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface OtpChannel {
  data object Success : OtpChannel
  data class Error(val error: ApiResult.Error) : OtpChannel
}
