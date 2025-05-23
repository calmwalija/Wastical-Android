package net.techandgraphics.wastemanagement.ui.screen.invoice

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface InvoiceEvent {

  data class AppState(val state: MainActivityState) : InvoiceEvent

  sealed interface Button : InvoiceEvent {
    data class Invoice(val payment: PaymentUiModel) : Button
    data class Share(val payment: PaymentUiModel) : Button
  }

  sealed interface GoTo : InvoiceEvent {
    data object BackHandler : GoTo
  }
}
