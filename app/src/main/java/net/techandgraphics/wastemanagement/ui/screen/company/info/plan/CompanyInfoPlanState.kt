package net.techandgraphics.wastemanagement.ui.screen.company.info.plan

import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel

sealed interface CompanyInfoPlanState {
  data object Loading : CompanyInfoPlanState
  data class Success(
    val company: CompanyUiModel,
    val plans: List<PaymentPlanUiModel> = listOf(),
  ) : CompanyInfoPlanState
}
