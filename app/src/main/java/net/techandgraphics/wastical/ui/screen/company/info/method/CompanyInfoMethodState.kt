package net.techandgraphics.wastical.ui.screen.company.info.method

import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentMethodWithGatewayAndPlanUiModel

sealed interface CompanyInfoMethodState {
  data object Loading : CompanyInfoMethodState
  data class Success(
    val company: CompanyUiModel,
    val methods: List<PaymentMethodWithGatewayAndPlanUiModel> = listOf(),
  ) : CompanyInfoMethodState
}
