package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.groupPaymentsByDate
import javax.inject.Inject

@HiltViewModel
class PaymentTimelineViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<PaymentTimelineState>(PaymentTimelineState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(PaymentTimelineEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    flowOfPayments()
  }

  private suspend fun flowOfPayments() {
    database.paymentDao.qPaymentWithAccountAndMethodWithGateway()
      .map { p0 -> p0.map { it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel() } }
      .collectLatest { p0 ->
        val payments = groupPaymentsByDate(p0)
        val company = database.companyDao.query().first().toCompanyUiModel()
        val filters = payments.map { it.key }.take(2).toSet()
        val filteredPayments = payments.filter { it.key in filters }
        _state.value = PaymentTimelineState.Success(
          payments = payments,
          company = company,
          filters = filters,
          filteredPayments = filteredPayments,
        )
      }
  }

  fun newState() = (_state.value as PaymentTimelineState.Success)

  private fun onButtonFilter(event: PaymentTimelineEvent.Button.Filter) =
    viewModelScope.launch {
      if (_state.value is PaymentTimelineState.Success) {
        val updatedFilters = newState().filters
          .toMutableSet()
          .apply {
            if (contains(event.item)) remove(event.item) else add(event.item)
          }
        if (updatedFilters.isEmpty()) return@launch
        _state.value = newState().copy(filters = updatedFilters)
        val filteredPayments = newState().payments.filter { it.key in newState().filters }
        _state.value = newState().copy(filteredPayments = filteredPayments)
      }
    }

  fun onEvent(event: PaymentTimelineEvent) {
    when (event) {
      PaymentTimelineEvent.Load -> onLoad()
      is PaymentTimelineEvent.Button.Filter -> onButtonFilter(event)
      else -> Unit
    }
  }
}
