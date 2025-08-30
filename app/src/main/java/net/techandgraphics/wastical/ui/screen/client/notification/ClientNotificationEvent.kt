package net.techandgraphics.wastical.ui.screen.client.notification

sealed interface ClientNotificationEvent {

  data object Load : ClientNotificationEvent

  sealed interface Button : ClientNotificationEvent {
    data object BackHandler : Button
    data class Sort(val sort: Boolean) : Button
  }

  sealed interface Input : ClientNotificationEvent {
    data class Query(val query: String) : Input
  }
}
