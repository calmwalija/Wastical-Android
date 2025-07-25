package net.techandgraphics.wastical.ui.screen.company.report

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel

sealed interface CompanyReportState {
  data object Loading : CompanyReportState
  data class Success(
    val company: CompanyUiModel,
    val accounts: List<AccountUiModel> = listOf(),
    val demographics: List<DemographicStreetUiModel> = listOf(),
  ) : CompanyReportState
}
