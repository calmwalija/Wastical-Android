package net.techandgraphics.wastical.ui.screen.company.report

import net.techandgraphics.wastical.data.local.database.dashboard.account.DemographicItem
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.query.PaymentWithAccountAndMethodWithGatewayQuery
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel

sealed interface CompanyReportState {
  data object Loading : CompanyReportState
  data class Success(
    val company: CompanyUiModel,
    val accounts: List<AccountUiModel> = listOf(),
    val demographics: List<DemographicItem> = listOf(),
    val monthAccountsCreated: List<MonthYear> = listOf(),
    val allMonthPayments: List<MonthYear> = listOf(),
    val filters: Set<MonthYear> = setOf(),
    val demographicFilters: Set<DemographicItem> = setOf(),
    // Dashboard metrics
    val totalAccounts: Int = 0,
    val activeAccounts: Int = 0,
    val inactiveAccounts: Int = 0,
    val newAccountsThisMonth: Int = 0,
    val expectedAmountThisMonth: Int = 0,
    val paidAccountsThisMonth: Int = 0,
    val paidAmountThisMonth: Int = 0,
    val unpaidAccountsThisMonth: Int = 0,
    val totalAmountReceivedAllTime: Int = 0,
    val overpaymentCount: Int = 0,
    val outstandingBalanceCount: Int = 0,
    val recentPayments: List<PaymentWithAccountAndMethodWithGatewayQuery> = listOf(),
  ) : CompanyReportState
}
