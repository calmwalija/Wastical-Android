package net.techandgraphics.wastical.ui.screen.auth.phone.verify

import net.techandgraphics.wastical.data.remote.ApiResult
import net.techandgraphics.wastical.keycloak.KeycloakErrorResponse

sealed interface VerifyPhoneChannel {

  data class Continue(val contact: String) : VerifyPhoneChannel

  sealed interface Response {
    data class Success(val sms: Sms) : VerifyPhoneChannel
    data class Failure(val error: ApiResult.Error) : VerifyPhoneChannel
  }

  sealed interface LoginException {
    data class Http(val code: Int, val message: String?) : LoginException
    data class IO(val message: String?) : LoginException
    data class Default(val message: String?) : LoginException
    data class KeycloakError(val error: KeycloakErrorResponse) : LoginException
  }
}
