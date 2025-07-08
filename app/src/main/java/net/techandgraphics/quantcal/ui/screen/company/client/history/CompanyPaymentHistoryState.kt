package net.techandgraphics.quantcal.ui.screen.company.client.history

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithMonthsCoveredUiModel

sealed interface CompanyPaymentHistoryState {
  object Loading : CompanyPaymentHistoryState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val payments: List<PaymentWithMonthsCoveredUiModel> = listOf(),
  ) : CompanyPaymentHistoryState
}
