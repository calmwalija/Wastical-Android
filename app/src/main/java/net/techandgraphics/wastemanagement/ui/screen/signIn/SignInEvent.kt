package net.techandgraphics.wastemanagement.ui.screen.signIn

sealed interface SignInEvent {

  sealed interface Input : SignInEvent {
    data class Credentials(val username: String, val password: String) : Input
  }

  sealed interface GoTo : SignInEvent {
    data object Main : GoTo
    data object SignUp : GoTo
  }

  sealed interface Button : SignInEvent {
    data object AccessToken : Button
  }
}
