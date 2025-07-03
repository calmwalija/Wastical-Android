package net.techandgraphics.quantcal.ui.screen.company.payment.verify

import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentRequestWithAccountUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

sealed interface CompanyVerifyPaymentState {
  data object Loading : CompanyVerifyPaymentState
  data class Success(
    val company: CompanyUiModel,
    val ofType: PaymentStatus = PaymentStatus.Approved,
    val pending: List<PaymentRequestWithAccountUiModel> = listOf(),
    val payments: List<PaymentWithAccountAndMethodWithGatewayUiModel> = listOf(),
  ) : CompanyVerifyPaymentState
}
