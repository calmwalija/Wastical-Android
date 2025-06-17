package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

sealed interface CompanyClientPlanState {

  data object Loading : CompanyClientPlanState

  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  ) : CompanyClientPlanState
}
