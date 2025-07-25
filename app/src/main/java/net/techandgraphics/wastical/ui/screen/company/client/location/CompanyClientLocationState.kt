package net.techandgraphics.wastical.ui.screen.company.client.location

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastical.domain.model.payment.CompanyLocationUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel

sealed interface CompanyClientLocationState {
  data object Loading : CompanyClientLocationState
  data class Success(
    val query: String = "",
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val companyLocation: CompanyLocationUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val accountDemographicArea: DemographicAreaUiModel,
    val accountDemographicStreet: DemographicStreetUiModel,
    val demographics: List<CompanyLocationWithDemographicUiModel> = listOf(),
  ) : CompanyClientLocationState
}
