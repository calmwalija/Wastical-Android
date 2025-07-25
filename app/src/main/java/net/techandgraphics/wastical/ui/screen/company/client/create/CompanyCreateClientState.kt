package net.techandgraphics.wastical.ui.screen.company.client.create

import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel

sealed interface CompanyCreateClientState {
  data object Loading : CompanyCreateClientState
  data class Success(
    val title: AccountTitle = AccountTitle.MR,
    val firstname: String = "",
    val lastname: String = "",
    val contact: String = "",
    val altContact: String = "",
    val planId: Long = 2,
    val company: CompanyUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  ) : CompanyCreateClientState
}
