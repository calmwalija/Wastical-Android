package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

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
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.relations.toEntity
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.ui.screen.company.payment.verify.CompanyVerifyPaymentEvent.Verify
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

  init {
    onEvent(CompanyVerifyPaymentEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    flowOfPayments()
  }

  private suspend fun flowOfPayments() {
    database.paymentDao.qPaymentWithAccountAndMethodWithGateway()
      .map { fromDb ->
        fromDb.map {
          it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
        }
      }
      .collectLatest { payments ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        _state.value = CompanyVerifyPaymentState.Success(
          company = company,
          payments = payments,
        )
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

  fun onEvent(event: CompanyVerifyPaymentEvent) {
    when (event) {
      CompanyVerifyPaymentEvent.Load -> onLoad()
      is Verify.Button.Status -> onVerifyStatusChange(event)
      else -> Unit
    }
  }
}
