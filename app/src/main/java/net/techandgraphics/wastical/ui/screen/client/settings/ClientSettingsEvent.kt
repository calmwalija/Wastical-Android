package net.techandgraphics.wastical.ui.screen.client.settings

sealed interface ClientSettingsEvent {

  data object Load : ClientSettingsEvent

  sealed interface Button : ClientSettingsEvent {
    data object BackHandler : Button
    data class DynamicColor(val isEnabled: Boolean) : Button
    data class DarkTheme(val isEnabled: Boolean) : Button
  }

  sealed interface Goto : ClientSettingsEvent {
    data object Settings : Goto
  }
}
