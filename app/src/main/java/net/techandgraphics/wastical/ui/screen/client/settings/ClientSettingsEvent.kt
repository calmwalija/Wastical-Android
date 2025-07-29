package net.techandgraphics.wastical.ui.screen.client.settings

sealed interface ClientSettingsEvent {

  data object Load : ClientSettingsEvent

  sealed interface Button : ClientSettingsEvent {
    data object BackHandler : Button
  }

  sealed interface Goto : ClientSettingsEvent {
    data object Settings : Goto
  }
}
