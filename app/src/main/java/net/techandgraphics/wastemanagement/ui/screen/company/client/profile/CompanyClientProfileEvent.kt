package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

sealed interface CompanyClientProfileEvent {
  data class Load(val id: Long) : CompanyClientProfileEvent

  sealed interface Option : CompanyClientProfileEvent {
    data object Payment : Option
    data object Plan : Option
    data object History : Option
    data object Pending : Option
    data object Location : Option
    data object Revoke : Option
  }

  sealed interface Button : CompanyClientProfileEvent {
    data object BackHandler : Button
  }
}
