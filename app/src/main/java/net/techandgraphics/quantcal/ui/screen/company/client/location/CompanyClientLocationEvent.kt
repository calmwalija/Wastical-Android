package net.techandgraphics.quantcal.ui.screen.company.client.location

sealed interface CompanyClientLocationEvent {
  data class Load(val id: Long) : CompanyClientLocationEvent

  sealed interface Goto : CompanyClientLocationEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }

  sealed interface Button : CompanyClientLocationEvent {
    data object Clear : Button
    data class Phone(val contact: String) : Button
  }

  sealed interface Input : CompanyClientLocationEvent {
    class Search(val query: String) : Input
  }
}
