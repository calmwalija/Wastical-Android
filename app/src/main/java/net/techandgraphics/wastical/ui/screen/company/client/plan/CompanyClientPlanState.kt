package net.techandgraphics.wastical.ui.screen.company.client.plan

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel

sealed interface CompanyClientPlanState {

  data object Loading : CompanyClientPlanState

  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  ) : CompanyClientPlanState
}
