package net.techandgraphics.wastical.ui.screen.company.client.browse

import net.techandgraphics.wastical.domain.model.search.SearchTagUiModel

sealed interface CompanyBrowseClientListEvent {

  sealed interface Button : CompanyBrowseClientListEvent {
    data object Filter : Button
    data class FilterBy(val id: Long) : Button
    data object HistoryTag : Button
    data class Tag(val tag: SearchTagUiModel) : Button
    data object Clear : Button
    data object ScheduleUpload : Button
  }

  sealed interface Goto : CompanyBrowseClientListEvent {
    data class Profile(val id: Long) : Goto
    data object BackHandler : Goto
  }

  sealed interface Input : CompanyBrowseClientListEvent {
    class Search(val query: String) : CompanyBrowseClientListEvent
  }
}
