package net.techandgraphics.wastemanagement.ui.screen.auth.signin

import net.techandgraphics.wastemanagement.data.remote.ApiResult
import net.techandgraphics.wastemanagement.keycloak.KeycloakErrorResponse

sealed interface SignInChannel {

  sealed interface Response {
    data object Success : SignInChannel
    data class Failure(val error: ApiResult.Error) : SignInChannel
  }

  sealed interface LoginException {
    data class Http(val code: Int, val message: String?) : LoginException
    data class IO(val message: String?) : LoginException
    data class Default(val message: String?) : LoginException
    data class KeycloakError(val error: KeycloakErrorResponse) : LoginException
  }
}
