package net.techandgraphics.wastemanagement.ui.screen.company.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.remote.onApiErrorHandler
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentRepository
import net.techandgraphics.wastemanagement.data.remote.toPaymentRequest
import net.techandgraphics.wastemanagement.domain.toPaymentAccountUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentViewModel @Inject constructor(
  private val database: AppDatabase,
  private val repository: PaymentRepository,
) : ViewModel() {

  private val _state = MutableStateFlow(CompanyPaymentState())
  private val _channel = Channel<CompanyPaymentChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart {
      viewModelScope.launch {
        launch { flowOfPayments() }
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyPaymentState(),
    )

  private fun onAppState(event: CompanyPaymentEvent.AppState) {
    _state.update { it.copy(state = event.state) }
  }

  private suspend fun flowOfPayments() {
    database.paymentDao.flowOfPaymentAccount()
      .map { fromDb -> fromDb.map { it.toPaymentAccountUiModel() } }
      .collectLatest { payments -> _state.update { it.copy(payments = payments) } }
  }

  private fun onPaymentStatus(event: CompanyPaymentEvent.Payment.Button.Status) =
    viewModelScope.launch {
      val request = event.payment.copy(status = event.status).toPaymentRequest()
      runCatching { repository.onPut(event.payment.id, request) }
        .onFailure { _channel.send(CompanyPaymentChannel.Payment.Failure(onApiErrorHandler(it))) }
        .onSuccess {
          it.map { it.toPaymentEntity() }.run {
            database.paymentDao.upsert(this)
            _channel.send(CompanyPaymentChannel.Payment.Success(this))
          }
        }
    }

  fun onEvent(event: CompanyPaymentEvent) {
    when (event) {
      is CompanyPaymentEvent.AppState -> onAppState(event)
      is CompanyPaymentEvent.Payment.Button.Status -> onPaymentStatus(event)
    }
  }
}
