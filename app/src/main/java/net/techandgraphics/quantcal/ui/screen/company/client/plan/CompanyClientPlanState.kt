package net.techandgraphics.quantcal.ui.screen.company.client.plan

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel

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
