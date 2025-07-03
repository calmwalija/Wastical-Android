package net.techandgraphics.quantcal.ui.screen.company.client.location

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel

sealed interface CompanyClientLocationState {
  data object Loading : CompanyClientLocationState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val accountDemographicArea: DemographicAreaUiModel,
    val accountDemographicStreet: DemographicStreetUiModel,
    val demographics: List<CompanyLocationWithDemographicUiModel> = listOf(),
  ) : CompanyClientLocationState
}
