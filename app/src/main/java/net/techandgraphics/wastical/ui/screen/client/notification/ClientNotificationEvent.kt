package net.techandgraphics.wastical.ui.screen.client.notification

sealed interface ClientNotificationEvent {

  data object Load : ClientNotificationEvent

  sealed interface Button : ClientNotificationEvent {
    data object BackHandler : Button
  }
}
