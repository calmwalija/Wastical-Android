package net.techandgraphics.wastical.ui.screen.auth.phone.otp

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface OtpChannel {
  data object Success : OtpChannel
  data class Error(val error: ApiResult.Error) : OtpChannel
}
