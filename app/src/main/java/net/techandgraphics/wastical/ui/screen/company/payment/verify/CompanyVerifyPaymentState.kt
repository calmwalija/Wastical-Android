package net.techandgraphics.wastical.ui.screen.company.payment.verify

import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface CompanyVerifyPaymentState {
  data object Loading : CompanyVerifyPaymentState
  data class Success(
    val company: CompanyUiModel,
    val ofType: PaymentStatus = PaymentStatus.Approved,
    val pending: List<PaymentRequestWithAccountUiModel> = listOf(),
    val payments: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
  ) : CompanyVerifyPaymentState
}
