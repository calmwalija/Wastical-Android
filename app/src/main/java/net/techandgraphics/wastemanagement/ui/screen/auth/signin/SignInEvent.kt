package net.techandgraphics.wastemanagement.ui.screen.auth.signin

sealed interface SignInEvent {

  sealed interface Input : SignInEvent {
    data class Credentials(val value: String, val type: Type) : Input
    enum class Type { ContactNumber, Password }
  }

  sealed interface GoTo : SignInEvent {
    data object Main : GoTo
  }

  sealed interface Button : SignInEvent {
    data object AccessToken : Button
  }
}
