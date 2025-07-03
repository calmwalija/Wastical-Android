package net.techandgraphics.quantcal.ui.screen.company.client.location

sealed interface CompanyClientLocationEvent {
  data class Load(val id: Long) : CompanyClientLocationEvent

  sealed interface Goto : CompanyClientLocationEvent {
    data object BackHandler : Goto
  }
}
