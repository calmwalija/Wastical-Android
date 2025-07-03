package net.techandgraphics.quantcal.ui.screen.company.payment.timeline

import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface PaymentTimelineState {
  data object Loading : PaymentTimelineState
  data class Success(
    val company: CompanyUiModel,
    val payments: Map<String, List<PaymentWithAccountAndMethodWithGatewayUiModel>>,
  ) :
    PaymentTimelineState
}
