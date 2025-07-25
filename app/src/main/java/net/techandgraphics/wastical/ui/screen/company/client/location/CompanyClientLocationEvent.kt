package net.techandgraphics.wastical.ui.screen.company.client.location

import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel

sealed interface CompanyClientLocationEvent {
  data class Load(val id: Long) : CompanyClientLocationEvent

  sealed interface Goto : CompanyClientLocationEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }

  sealed interface Button : CompanyClientLocationEvent {
    data object Clear : Button
    data class Phone(val contact: String) : Button
    data class Change(val demographicStreet: DemographicStreetUiModel) : Button
  }

  sealed interface Input : CompanyClientLocationEvent {
    class Search(val query: String) : Input
  }
}
