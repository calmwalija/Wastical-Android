package net.techandgraphics.wastemanagement.ui.screen.client.invoice

import android.app.Application
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
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import net.techandgraphics.wastemanagement.preview
import net.techandgraphics.wastemanagement.share
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.pdf.invoiceToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClientInvoiceViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(ClientInvoiceState())
  private val _channel = Channel<ClientInvoiceChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart { getInvoices() }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = ClientInvoiceState(),
    )

  private fun getInvoices() = viewModelScope.launch {
    database.paymentDao.flowOfAllInvoices()
      .map { dbInvoices -> dbInvoices.map { it.toPaymentUiModel() } }
      .collectLatest { invoices -> _state.update { it.copy(invoices = invoices) } }
  }

  private fun onAppState(event: ClientInvoiceEvent.AppState) {
    _state.update { it.copy(state = event.state) }
  }

  private fun onInvoiceToPdf(payment: PaymentUiModel, onEvent: (File?) -> Unit) =
    with(state.value.state) {
      invoiceToPdf(
        context = application,
        account = accounts.first(),
        accountContact = accountContacts.first { it.primary },
        payment = payment,
        paymentPlan = paymentPlans.first(),
        company = companies.first(),
        companyContact = companyContacts.first { it.primary },
        paymentMethod = state.value.state.paymentMethods.first { it.id == payment.paymentMethodId },
        onEvent = onEvent,
        // TODO
        paymentGateway = paymentGateways.first(),
      )
    }

  private fun onPaymentShare(event: ClientInvoiceEvent.Button.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  private fun onInvoice(event: ClientInvoiceEvent.Button.Invoice) {
    onInvoiceToPdf(event.payment) { file ->
      file?.preview(application)
    }
  }

  fun onEvent(event: ClientInvoiceEvent) {
    when (event) {
      is ClientInvoiceEvent.AppState -> onAppState(event)
      is ClientInvoiceEvent.Button.Share -> onPaymentShare(event)
      is ClientInvoiceEvent.Button.Invoice -> onInvoice(event)

      else -> Unit
    }
  }
}
