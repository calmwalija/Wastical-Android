package net.techandgraphics.wastemanagement.ui.screen.invoice

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface InvoiceEvent {
  sealed interface Button : InvoiceEvent {
    data class Tap(val payment: PaymentUiModel) : Button
    data class Share(val payment: PaymentUiModel) : Button
  }
}
