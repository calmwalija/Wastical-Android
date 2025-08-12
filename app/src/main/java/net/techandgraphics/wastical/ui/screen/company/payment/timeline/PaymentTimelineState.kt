package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import java.time.LocalDate

typealias PaymentTimeline = Map<PaymentDateTime, List<PaymentWithAccountAndMethodWithGatewayUiModel>>

sealed interface PaymentTimelineState {
  data object Loading : PaymentTimelineState
  data class Success(
    val company: CompanyUiModel,
    val payments: PaymentTimeline,
    val filteredPayments: PaymentTimeline = mapOf(),
    val filters: Set<PaymentDateTime> = setOf(),
  ) : PaymentTimelineState
}

data class PaymentDateTime(
  val date: LocalDate,
  val time: Long,
)
