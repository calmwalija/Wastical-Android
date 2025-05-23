package net.techandgraphics.wastemanagement.ui.screen.home

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
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.preview
import net.techandgraphics.wastemanagement.share
import net.techandgraphics.wastemanagement.ui.screen.invoice.pdf.invoiceToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel class HomeViewModel @Inject constructor(
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(HomeState())
  private val _channel = Channel<HomeChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state.onStart {
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000L),
    initialValue = HomeState(),
  )

  private fun onAppState(event: HomeEvent.AppState) {
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

  private fun onPaymentTap(event: HomeEvent.Button.Payment.Invoice) {
    when (event.payment.status) {
      PaymentStatus.Approved -> onInvoiceToPdf(event.payment) { file ->
        file?.preview(application)
      }

      else -> Unit
    }
  }

  private fun onPaymentShare(event: HomeEvent.Button.Payment.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  fun onEvent(event: HomeEvent) {
    when (event) {
      is HomeEvent.Button.Payment.Invoice -> onPaymentTap(event)
      is HomeEvent.Button.Payment.Share -> onPaymentShare(event)
      is HomeEvent.AppState -> onAppState(event)
      else -> Unit
    }
  }
}
