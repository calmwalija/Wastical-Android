package net.techandgraphics.quantcal.ui.screen.client.home

import net.techandgraphics.quantcal.domain.model.account.AccountContactUiModel
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyBinCollectionUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyContactUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithMonthsCoveredUiModel

sealed interface ClientHomeState {
  data object Loading : ClientHomeState
  data class Success(
    val searchQuery: String = "",
    val company: CompanyUiModel,
    val paymentPlan: PaymentPlanUiModel,
    val account: AccountUiModel,
    val lastMonthCovered: PaymentMonthCoveredUiModel? = null,
    val paymentMethods: List<PaymentMethodWithGatewayAndPlanUiModel> = listOf(),
    val invoices: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
    val payments: List<PaymentWithMonthsCoveredUiModel> = listOf(),
    val companyBinCollections: List<CompanyBinCollectionUiModel> = listOf(),
    val accountContacts: List<AccountContactUiModel> = listOf(),
    val companyContacts: List<CompanyContactUiModel> = listOf(),
  ) : ClientHomeState
}
