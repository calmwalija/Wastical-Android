package net.techandgraphics.wastemanagement.ui.screen.auth.phone.verify

import net.techandgraphics.wastemanagement.data.remote.ApiResult
import net.techandgraphics.wastemanagement.keycloak.KeycloakErrorResponse

sealed interface VerifyPhoneChannel {

  sealed interface Response {
    data class Success(val phone: String) : VerifyPhoneChannel
    data class Failure(val error: ApiResult.Error) : VerifyPhoneChannel
  }

  sealed interface LoginException {
    data class Http(val code: Int, val message: String?) : LoginException
    data class IO(val message: String?) : LoginException
    data class Default(val message: String?) : LoginException
    data class KeycloakError(val error: KeycloakErrorResponse) : LoginException
  }
}
