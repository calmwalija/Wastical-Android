package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel

sealed interface CompanyPaymentPerLocationState {
  data object Loading : CompanyPaymentPerLocationState
  data class Success(
    val payment4CurrentLocationMonth: List<Payment4CurrentLocationMonth> = listOf(),
    val company: CompanyUiModel,
  ) : CompanyPaymentPerLocationState
}
