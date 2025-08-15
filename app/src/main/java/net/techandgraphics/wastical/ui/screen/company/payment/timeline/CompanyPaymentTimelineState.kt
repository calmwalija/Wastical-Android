package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel

typealias PagingPayments = Flow<PagingData<PaymentWithAccountAndMethodWithGatewayUiModel>>

sealed interface CompanyPaymentTimelineState {
  data object Loading : CompanyPaymentTimelineState
  data class Success(
    val company: CompanyUiModel,
    val payments: PagingPayments = flow { },
    val query: String = "",
    val sort: Boolean = true,
  ) : CompanyPaymentTimelineState
}
