package net.techandgraphics.quantcal.ui.screen.company.location.browse

import net.techandgraphics.quantcal.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.quantcal.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel

sealed interface CompanyBrowseLocationState {
  data object Loading : CompanyBrowseLocationState
  data class Success(
    val payment4CurrentLocationMonth: List<Payment4CurrentLocationMonth> = listOf(),
    val company: CompanyUiModel,
    val query: String = "",
    val filters: Set<Long> = setOf(),
    val monthYear: MonthYear,
  ) : CompanyBrowseLocationState
}
