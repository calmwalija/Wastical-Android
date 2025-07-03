package net.techandgraphics.quantcal.ui.screen.company.client.create

import net.techandgraphics.quantcal.data.local.database.account.AccountTitle
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel

sealed interface CompanyCreateClientState {
  data object Loading : CompanyCreateClientState
  data class Success(
    val title: AccountTitle = AccountTitle.MR,
    val firstname: String = "",
    val lastname: String = "",
    val contact: String = "",
    val altContact: String = "",
    val planId: Long = 2,
    val companyLocationId: Long = -1,
    val company: CompanyUiModel,
    val demographics: List<CompanyLocationWithDemographicUiModel> = listOf(),
    val paymentPlans: List<PaymentPlanUiModel> = listOf(),
  ) : CompanyCreateClientState
}
