package net.techandgraphics.wastemanagement.ui.screen.company.info

import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel

sealed interface CompanyInfoState {
  data object Loading : CompanyInfoState
  data class Success(
    val company: CompanyUiModel,
    val contacts: List<CompanyContactUiModel>,
  ) : CompanyInfoState
}
