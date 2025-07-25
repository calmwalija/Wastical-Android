package net.techandgraphics.wastical.ui.screen.client.invoice

import net.techandgraphics.wastical.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface ClientInvoiceState {
  data object Loading : ClientInvoiceState

  data class Success(
    val company: CompanyUiModel,
    val paymentPlan: PaymentPlanUiModel,
    val account: AccountUiModel,
    val paymentMethods: List<PaymentMethodWithGatewayAndPlanUiModel> = listOf(),
    val invoices: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
    val accountContacts: List<AccountContactUiModel> = listOf(),
    val companyContacts: List<CompanyContactUiModel> = listOf(),
  ) : ClientInvoiceState
}
