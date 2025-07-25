package net.techandgraphics.wastical.ui.screen.company.info.plan

import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel

sealed interface CompanyInfoPlanState {
  data object Loading : CompanyInfoPlanState
  data class Success(
    val company: CompanyUiModel,
    val plans: List<PaymentPlanUiModel> = listOf(),
  ) : CompanyInfoPlanState
}
