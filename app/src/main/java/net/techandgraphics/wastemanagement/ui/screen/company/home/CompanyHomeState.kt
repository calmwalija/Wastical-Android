package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.PaymentPlanAgainstAccounts
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel

sealed interface CompanyHomeState {
  data object Loading : CompanyHomeState

  data class Success(
    val payment4CurrentMonth: Payment4CurrentMonth,
    val payment4CurrentLocationMonth: List<Payment4CurrentLocationMonth> = listOf(),
    val company: CompanyUiModel,
    val companyContact: CompanyContactUiModel,
    val account: AccountUiModel,
    val accountsSize: Int,
    val expectedAmountToCollect: Int,
    val paymentPlanAgainstAccounts: List<PaymentPlanAgainstAccounts>,
  ) : CompanyHomeState
}
