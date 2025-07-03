package net.techandgraphics.quantcal.ui.screen.company.location.overview

import net.techandgraphics.quantcal.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.AccountSortOrder
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.quantcal.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.quantcal.domain.model.payment.CompanyLocationUiModel

sealed interface CompanyPaymentLocationOverviewState {
  data object Loading : CompanyPaymentLocationOverviewState
  data class Success(
    val company: CompanyUiModel,
    val demographicStreet: DemographicStreetUiModel,
    val demographicArea: DemographicAreaUiModel,
    val payment4CurrentMonth: Payment4CurrentMonth,
    val accounts: List<AccountWithPaymentStatusUiModel> = listOf(),
    val expectedAmountToCollect: Int,
    val companyLocation: CompanyLocationUiModel,
    val sortBy: AccountSortOrder = AccountSortOrder.Paid,
    val monthYear: MonthYear,
  ) : CompanyPaymentLocationOverviewState
}
