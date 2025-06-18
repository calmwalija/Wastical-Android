package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface CompanyVerifyPaymentState {
  data object Loading : CompanyVerifyPaymentState
  data class Success(
    val company: CompanyUiModel,
    val payments: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
  ) : CompanyVerifyPaymentState
}
