package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentWithAccountAndMethodWithGatewayUiModel
import java.time.LocalDate

typealias PagingPayments = Flow<PagingData<PaymentWithAccountAndMethodWithGatewayUiModel>>

sealed interface CompanyPaymentTimelineState {
  data object Loading : CompanyPaymentTimelineState
  data class Success(
    val company: CompanyUiModel,
    val payments: PagingPayments = flow { },
    val dateTimeItems: List<PaymentDateTime> = listOf(),
    val filters: Set<PaymentDateTime> = setOf(),
    val query: String = "",
    val isRefreshing: Boolean = false,
    val fromTs: Long? = null,
    val toTs: Long? = null,
    val sortDesc: Boolean = true,
  ) : CompanyPaymentTimelineState
}

enum class DateRangePreset { All, Today, Last7Days, ThisMonth, ThisYear }

data class PaymentDateTime(
  val date: LocalDate,
  val time: List<Long>,
)
