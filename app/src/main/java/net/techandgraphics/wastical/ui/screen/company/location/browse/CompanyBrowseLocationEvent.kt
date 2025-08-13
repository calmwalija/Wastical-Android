package net.techandgraphics.wastical.ui.screen.company.location.browse

sealed interface CompanyBrowseLocationEvent {

  data object Load : CompanyBrowseLocationEvent

  sealed interface Button : CompanyBrowseLocationEvent {
    data object BackHandler : CompanyBrowseLocationEvent
    data object Clear : Button
    data object Filter : Button
  }

  sealed interface Goto : CompanyBrowseLocationEvent {
    data class LocationOverview(val id: Long) : Goto
  }

  sealed interface Input : CompanyBrowseLocationEvent {
    class Search(val query: String) : Input
  }

  data class SortBy(val sort: LocationSortOrder) : CompanyBrowseLocationEvent
}
