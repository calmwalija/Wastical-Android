package net.techandgraphics.wastemanagement.ui.screen.company.client.browse

sealed interface CompanyBrowseClientListEvent {

  sealed interface Button : CompanyBrowseClientListEvent {
    data object Filter : Button
    data object Clear : Button
  }

  sealed interface Goto : CompanyBrowseClientListEvent {
    data class Profile(val id: Long) : Goto
    data object Create : Goto
    data object BackHandler : Goto
  }

  sealed interface Input : CompanyBrowseClientListEvent {
    class Search(val query: String) : CompanyBrowseClientListEvent
  }
}
