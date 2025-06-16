package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

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
import net.techandgraphics.wastemanagement.data.local.database.relations.toEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent.AppState
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent.Payment
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent.Verify
import javax.inject.Inject

@HiltViewModel
class CompanyVerifyPaymentViewModel @Inject constructor(
  private val database: AppDatabase,
  private val api: PaymentApi,
) : ViewModel() {

  private val _state = MutableStateFlow(CompanyVerifyPaymentState())
  private val _channel = Channel<CompanyVerifyPaymentChannel>()
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
      initialValue = CompanyVerifyPaymentState(),
    )

  private fun getPaymentMethod() = viewModelScope.launch {
  }

  private fun onAppState(event: AppState) {
    _state.update { it.copy(state = event.state) }
  }

  private suspend fun flowOfPayments() {
    database.paymentDao.qPaymentWithAccountAndMethodWithGateway()
      .map { fromDb ->
        fromDb.map {
          it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
        }
      }
      .collectLatest { payments -> _state.update { it.copy(payments = payments) } }
  }

  private fun onPaymentStatus(event: Payment.Button.Status) =
    viewModelScope.launch {
      // TODO - This should be done through a worker
//      val request = event.payment.copy(status = event.status).toPaymentRequest()
//      runCatching { api.put(event.payment.id, request) }
//        .onFailure { _channel.send(CompanyVerifyPaymentChannel.Payment.Failure(mapApiError(it))) }
//        .onSuccess {
//          it.map { it.toPaymentEntity() }.run {
//            database.paymentDao.upsert(this)
//            _channel.send(CompanyVerifyPaymentChannel.Payment.Success(this))
//          }
//        }
    }

  private fun onVerifyStatusChange(event: Verify.Button.Status) =
    viewModelScope.launch {
      database.paymentDao.qPaymentWithAccountAndMethodWithGateway(event.status.name)
        .map { fromDb ->
          fromDb.map {
            it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
          }
        }
        .collectLatest { payments -> _state.update { it.copy(payments = payments) } }
    }

  fun onEvent(event: CompanyVerifyPaymentEvent) {
    when (event) {
      is AppState -> onAppState(event)
      is Payment.Button.Status -> onPaymentStatus(event)
      is Verify.Button.Status -> onVerifyStatusChange(event)
    }
  }
}
