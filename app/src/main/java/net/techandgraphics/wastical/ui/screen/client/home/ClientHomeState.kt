package net.techandgraphics.wastical.ui.screen.client.home

import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyBinCollectionUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface ClientHomeState {
  data object Loading : ClientHomeState
  data class Success(
    val searchQuery: String = "",
    val company: CompanyUiModel,
    val paymentPlan: PaymentPlanUiModel,
    val account: AccountUiModel,
    val lastMonthCovered: PaymentMonthCoveredUiModel? = null,
    val monthsOutstanding: Int = 0,
    val outstandingMonths: List<MonthYear> = listOf(),
    val paymentMethods: List<PaymentMethodWithGatewayAndPlanUiModel> = listOf(),
    val invoices: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
    val payments: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
    val paymentRequests: List<PaymentRequestWithAccountUiModel> = listOf(),
    val companyBinCollections: List<CompanyBinCollectionUiModel> = listOf(),
    val accountContacts: List<AccountContactUiModel> = listOf(),
    val companyContacts: List<CompanyContactUiModel> = listOf(),
  ) : ClientHomeState
}
