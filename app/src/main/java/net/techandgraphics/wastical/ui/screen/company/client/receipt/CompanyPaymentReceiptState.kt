package net.techandgraphics.wastical.ui.screen.company.client.receipt

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithMonthsCoveredUiModel

sealed interface CompanyPaymentReceiptState {
  object Loading : CompanyPaymentReceiptState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val payments: List<PaymentWithMonthsCoveredUiModel> = listOf(),
  ) : CompanyPaymentReceiptState
}
