package net.techandgraphics.wastemanagement.ui.screen.signIn

import net.techandgraphics.wastemanagement.keycloak.KeycloakErrorResponse

sealed interface SignInChannel {

  sealed interface Response {
    data object Success : SignInChannel
    data class Failure(val exception: LoginException) : SignInChannel
  }

  sealed interface LoginException {
    data class Http(val code: Int, val message: String?) : LoginException
    data class IO(val message: String?) : LoginException
    data class Default(val message: String?) : LoginException
    data class KeycloakError(val error: KeycloakErrorResponse) : LoginException
  }
}
