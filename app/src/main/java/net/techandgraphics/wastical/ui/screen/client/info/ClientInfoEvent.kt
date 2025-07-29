package net.techandgraphics.wastical.ui.screen.client.info

sealed interface ClientInfoEvent {

  data object Load : ClientInfoEvent

  sealed interface Button : ClientInfoEvent {
    data object BackHandler : Button
    data object Submit : Button
  }

  sealed interface Input : ClientInfoEvent {
    enum class OfType { FName, LName, Contact, AltContact, Email, Title }
    data class Type(val newValue: String, val ofType: OfType) : Input
  }
}
