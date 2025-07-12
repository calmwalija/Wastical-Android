package net.techandgraphics.quantcal.ui.screen.company.client.info

import net.techandgraphics.quantcal.domain.model.account.AccountContactUiModel
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.ui.screen.account4Preview

sealed interface CompanyClientInfoState {
  data object Loading : CompanyClientInfoState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val oldAccount: AccountUiModel = account4Preview,
    val demographic: CompanyLocationWithDemographicUiModel,
    val contacts: List<AccountContactUiModel> = listOf(),
  ) : CompanyClientInfoState
}
