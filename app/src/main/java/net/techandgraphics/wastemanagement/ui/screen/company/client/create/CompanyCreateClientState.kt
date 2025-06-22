package net.techandgraphics.wastemanagement.ui.screen.company.client.create

import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.CompanyLocationWithDemographicUiModel

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
