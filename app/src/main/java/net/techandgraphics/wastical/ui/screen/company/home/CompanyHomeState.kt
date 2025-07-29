package net.techandgraphics.wastical.ui.screen.company.home

import net.techandgraphics.wastical.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastical.data.local.database.dashboard.payment.PaymentPlanAgainstAccounts
import net.techandgraphics.wastical.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentDao
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface CompanyHomeState {
  data object Loading : CompanyHomeState

  data class Success(
    val payment4CurrentMonth: Payment4CurrentMonth,
    val payment4CurrentLocationMonth: List<Payment4CurrentLocationMonth> = listOf(),
    val company: CompanyUiModel,
    val companyContact: CompanyContactUiModel,
    val account: AccountUiModel,
    val accountsSize: Int,
    val upfrontPayments: List<PaymentDao.UpfrontPayment> = listOf(),
    val monthYear: MonthYear,
    val proofOfPayments: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
    val expectedAmountToCollect: Int,
    val paymentPlanAgainstAccounts: List<PaymentPlanAgainstAccounts>,
    val allMonthsPayments: List<MonthYearPayment4Month> = listOf(),
    val timeline: List<PaymentWithAccountAndMethodWithGatewayUiModel>,
  ) : CompanyHomeState
}
