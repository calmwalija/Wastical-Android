package net.techandgraphics.wastemanagement.ui.screen.invoice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow(InvoiceState())
  private val _channel = Channel<InvoiceChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart {
      getInvoices()
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = InvoiceState(),
    )

  private suspend fun getInvoices() {
    val invoices = database.paymentDao.invoices().map { it.toPaymentUiModel() }
    _state.update { it.copy(invoices = invoices) }
  }

  fun onEvent(event: InvoiceEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
