package net.techandgraphics.wastical.ui.screen.client.info

sealed interface ClientInfoEvent {

  data class Load(val id: Long) : ClientInfoEvent

  sealed interface Button : ClientInfoEvent {
    data object BackHandler : Button
    data object Submit : Button
  }

  sealed interface Input : ClientInfoEvent {
    enum class OfType { FName, LName, Title }
    data class Type(val newValue: String, val ofType: OfType) : Input
  }
}
