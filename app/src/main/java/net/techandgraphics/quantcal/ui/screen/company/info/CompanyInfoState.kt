package net.techandgraphics.quantcal.ui.screen.company.info

import net.techandgraphics.quantcal.domain.model.company.CompanyContactUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel

sealed interface CompanyInfoState {
  data object Loading : CompanyInfoState
  data class Success(
    val company: CompanyUiModel,
    val contacts: List<CompanyContactUiModel>,
  ) : CompanyInfoState
}
