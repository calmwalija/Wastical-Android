package net.techandgraphics.quantcal.ui.screen.company.client.invoice

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithMonthsCoveredUiModel

sealed interface CompanyPaymentInvoiceState {
  object Loading : CompanyPaymentInvoiceState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val payments: List<PaymentWithMonthsCoveredUiModel> = listOf(),
  ) : CompanyPaymentInvoiceState
}
