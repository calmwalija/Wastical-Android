package net.techandgraphics.quantcal.ui.screen.company.info.method

import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentMethodWithGatewayUiModel

sealed interface CompanyInfoMethodState {
  data object Loading : CompanyInfoMethodState
  data class Success(
    val company: CompanyUiModel,
    val methods: List<PaymentMethodWithGatewayUiModel> = listOf(),
  ) : CompanyInfoMethodState
}
