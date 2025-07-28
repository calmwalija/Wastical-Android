package net.techandgraphics.wastical.ui.screen.auth.phone.verify

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface VerifyPhoneChannel {

  data class Continue(val contact: String) : VerifyPhoneChannel

  sealed interface Response : VerifyPhoneChannel {
    data class Success(val sms: Sms) : Response
    data class Failure(val error: ApiResult.Error) : Response
  }
}
