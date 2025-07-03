package net.techandgraphics.quantcal.ui.screen.company.home

import net.techandgraphics.quantcal.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.PaymentPlanAgainstAccounts
import net.techandgraphics.quantcal.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentDao
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyContactUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

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
    val pending: List<PaymentRequestUiModel> = listOf(),
    val expectedAmountToCollect: Int,
    val paymentPlanAgainstAccounts: List<PaymentPlanAgainstAccounts>,
    val allMonthsPayments: List<MonthYearPayment4Month> = listOf(),
    val timeline: List<PaymentWithAccountAndMethodWithGatewayUiModel>,
  ) : CompanyHomeState
}
