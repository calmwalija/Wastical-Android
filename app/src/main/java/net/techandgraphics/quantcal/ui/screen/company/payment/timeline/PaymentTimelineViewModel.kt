package net.techandgraphics.quantcal.ui.screen.company.payment.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.relations.toEntity
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.groupPaymentsByDate
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
        _state.value = PaymentTimelineState.Success(payments = payments, company = company)
      }
  }

  fun onEvent(event: PaymentTimelineEvent) {
    when (event) {
      PaymentTimelineEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
