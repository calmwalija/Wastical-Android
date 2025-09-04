package net.techandgraphics.wastical.ui.screen.auth.phone.load

sealed interface LoadChannel {
  data object Success : LoadChannel
  data object NoAccount : LoadChannel
  data class NoToken(val contact: String) : LoadChannel
  data class Otp(val contact: String) : LoadChannel
  data class Error(val error: Throwable) : LoadChannel
}
