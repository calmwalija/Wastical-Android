package net.techandgraphics.quantcal.ui.screen.company.client.profile

sealed interface CompanyClientProfileEvent {
  data class Load(val id: Long) : CompanyClientProfileEvent

  sealed interface Option : CompanyClientProfileEvent {
    data object Payment : Option
    data object Plan : Option
    data object History : Option
    data object Invoice : Option
    data object Pending : Option
    data object Location : Option
    data object Info : Option
    data object Revoke : Option
  }

  sealed interface Goto : CompanyClientProfileEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }

  sealed interface Button : CompanyClientProfileEvent {
    data class Phone(val contact: String) : Button
  }
}
