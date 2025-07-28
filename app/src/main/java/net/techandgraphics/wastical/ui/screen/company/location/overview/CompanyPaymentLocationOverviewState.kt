package net.techandgraphics.wastical.ui.screen.company.location.overview

import net.techandgraphics.wastical.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastical.data.local.database.dashboard.payment.AccountSortOrder
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.domain.model.account.AccountWithPaymentStatusUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastical.domain.model.payment.CompanyLocationUiModel

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
    val sortBy: AccountSortOrder = AccountSortOrder.Unpaid,
    val monthYear: MonthYear,
  ) : CompanyPaymentLocationOverviewState
}
