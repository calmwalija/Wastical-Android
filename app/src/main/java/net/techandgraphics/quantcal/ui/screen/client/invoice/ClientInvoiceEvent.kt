package net.techandgraphics.quantcal.ui.screen.client.invoice

import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel

sealed interface ClientInvoiceEvent {

  data class Load(val id: Long) : ClientInvoiceEvent

  sealed interface Button : ClientInvoiceEvent {
    data class Invoice(val payment: PaymentUiModel) : Button
    data class Share(val payment: PaymentUiModel) : Button
  }

  sealed interface GoTo : ClientInvoiceEvent {
    data object BackHandler : GoTo
  }
}
