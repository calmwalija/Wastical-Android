package net.techandgraphics.quantcal.ui.screen.company.payment.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.relations.toEntity
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentRequestWithAccountUiModel
import net.techandgraphics.quantcal.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent.Verify
import javax.inject.Inject

@HiltViewModel
class CompanyVerifyPaymentViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyVerifyPaymentState>(CompanyVerifyPaymentState.Loading)
  private val _channel = Channel<CompanyVerifyPaymentChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()

  private fun onLoad(ofType: String) = viewModelScope.launch {
    database.paymentRequestDao.qFlowWithAccount()
      .map { entity -> entity.map { it.toPaymentRequestWithAccountUiModel() } }
      .collectLatest { pending ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        _state.value = CompanyVerifyPaymentState.Success(
          company = company,
          pending = pending,
          ofType = PaymentStatus.valueOf(ofType),
        )
        flowOfPayments()
      }
  }

  private suspend fun flowOfPayments() {
    database.paymentDao.qPaymentWithAccountAndMethodWithGateway()
      .map { fromDb ->
        fromDb.map {
          it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
        }
      }
      .collectLatest { payments ->
        if (_state.value is CompanyVerifyPaymentState.Success) {
          _state.value =
            (_state.value as CompanyVerifyPaymentState.Success).copy(payments = payments)
        }
      }
  }

  private fun onVerifyStatusChange(event: Verify.Button.Status) =
    viewModelScope.launch {
      database.paymentDao.qPaymentWithAccountAndMethodWithGateway(event.status.name)
        .map { fromDb ->
          fromDb.map {
            it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
          }
        }
        .collectLatest { payments ->
          if (_state.value is CompanyVerifyPaymentState.Success) {
            _state.value = (_state.value as CompanyVerifyPaymentState.Success).copy(
              payments = payments,
            )
          }
        }
    }

  private fun onButtonStatus(event: CompanyVerifyPaymentEvent.Button.Status) {
    if (_state.value is CompanyVerifyPaymentState.Success) {
      _state.value = (_state.value as CompanyVerifyPaymentState.Success).copy(
        ofType = event.status,
      )
    }
  }

  fun onEvent(event: CompanyVerifyPaymentEvent) {
    when (event) {
      is CompanyVerifyPaymentEvent.Load -> onLoad(event.ofType)
      is Verify.Button.Status -> onVerifyStatusChange(event)
      is CompanyVerifyPaymentEvent.Button.Status -> onButtonStatus(event)
      else -> Unit
    }
  }
}
