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
class InvoiceViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(InvoiceState())
  private val _channel = Channel<InvoiceChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart { getInvoices() }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = InvoiceState(),
    )

  private fun getInvoices() = viewModelScope.launch {
    database.paymentDao.flowOfAllInvoices()
      .map { dbInvoices -> dbInvoices.map { it.toPaymentUiModel() } }
      .collectLatest { invoices -> _state.update { it.copy(invoices = invoices) } }
  }

  private fun onAppState(event: InvoiceEvent.AppState) {
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
        onEvent = onEvent,
      )
    }

  private fun onPaymentShare(event: InvoiceEvent.Button.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  private fun onInvoice(event: InvoiceEvent.Button.Invoice) {
    onInvoiceToPdf(event.payment) { file ->
      file?.preview(application)
    }
  }

  fun onEvent(event: InvoiceEvent) {
    when (event) {
      is InvoiceEvent.AppState -> onAppState(event)
      is InvoiceEvent.Button.Share -> onPaymentShare(event)
      is InvoiceEvent.Button.Invoice -> onInvoice(event)

      else -> Unit
    }
  }
}
