package net.techandgraphics.wastemanagement.ui.screen.client.invoice

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface ClientInvoiceEvent {

  data class AppState(val state: MainActivityState) : ClientInvoiceEvent

  sealed interface Button : ClientInvoiceEvent {
    data class Invoice(val payment: PaymentUiModel) : Button
    data class Share(val payment: PaymentUiModel) : Button
  }

  sealed interface GoTo : ClientInvoiceEvent {
    data object BackHandler : GoTo
  }
}
