package net.techandgraphics.wastical.ui.screen.company.info

import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel

sealed interface CompanyInfoState {
  data object Loading : CompanyInfoState
  data class Success(
    val company: CompanyUiModel,
    val contacts: List<CompanyContactUiModel>,
  ) : CompanyInfoState
}
