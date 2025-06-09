package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.PaidThisMonthIndicator
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.DailyPaymentSummary
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.StreetPaidThisMonthIndicator
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyHomeState {
  data object Loading : CompanyHomeState

  data class Success(
    val payments: List<PaymentAccountUiModel> = listOf(),
    val state: MainActivityState,
    val paidThisMonth: PaidThisMonthIndicator,
    val streetPaidThisMonth: List<StreetPaidThisMonthIndicator> = listOf(),
    val dailyPayments: List<DailyPaymentSummary> = listOf(),
  ) : CompanyHomeState
}
