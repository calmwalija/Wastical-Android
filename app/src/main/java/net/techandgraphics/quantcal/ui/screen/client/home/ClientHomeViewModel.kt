package net.techandgraphics.quantcal.ui.screen.client.home

import android.app.Application
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
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.preview
import net.techandgraphics.quantcal.share
import net.techandgraphics.quantcal.ui.screen.client.invoice.pdf.invoiceToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel class ClientHomeViewModel @Inject constructor(
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(ClientHomeState())
  private val _channel = Channel<ClientHomeChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state.onStart {
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000L),
    initialValue = ClientHomeState(),
  )

  private fun onAppState(event: ClientHomeEvent.AppState) {
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

  private fun onPaymentTap(event: ClientHomeEvent.Button.Payment.Invoice) {
    when (event.payment.status) {
      PaymentStatus.Approved -> onInvoiceToPdf(event.payment) { file ->
        file?.preview(application)
      }

      else -> Unit
    }
  }

  private fun onPaymentShare(event: ClientHomeEvent.Button.Payment.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  fun onEvent(event: ClientHomeEvent) {
    when (event) {
      is ClientHomeEvent.Button.Payment.Invoice -> onPaymentTap(event)
      is ClientHomeEvent.Button.Payment.Share -> onPaymentShare(event)
      is ClientHomeEvent.AppState -> onAppState(event)
      else -> Unit
    }
  }
}
