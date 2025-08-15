package net.techandgraphics.wastical.ui.screen.company.notification

sealed interface CompanyNotificationEvent {
  data object Load : CompanyNotificationEvent

  sealed interface Button : CompanyNotificationEvent {
    data object BackHandler : Button
    data class Sort(val sort: Boolean) : Button
  }

  sealed interface Input : CompanyNotificationEvent {
    data class Query(val query: String) : Input
  }
}
