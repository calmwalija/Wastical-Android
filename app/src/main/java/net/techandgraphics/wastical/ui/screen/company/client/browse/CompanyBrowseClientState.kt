package net.techandgraphics.wastical.ui.screen.company.client.browse

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel
import net.techandgraphics.wastical.domain.model.account.AccountRequestUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastical.domain.model.search.SearchTagUiModel

sealed interface CompanyBrowseClientState {
  data object Loading : CompanyBrowseClientState
  data class Success(
    val accounts: Flow<PagingData<AccountInfoUiModel>> = flow { },
    val company: CompanyUiModel,
    val query: String = "",
    val demographicAreas: List<DemographicAreaUiModel> = listOf(),
    val demographicStreets: List<DemographicStreetUiModel> = listOf(),
    val filters: Set<Long> = setOf(),
    val searchHistoryTags: List<SearchTagUiModel> = listOf(),
    val accountRequests: List<AccountRequestUiModel> = listOf(),
  ) : CompanyBrowseClientState
}
