package net.techandgraphics.wastical.ui.screen.client.receipt

import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

sealed interface ClientReceiptEvent {

  data class Load(val id: Long) : ClientReceiptEvent

  sealed interface Button : ClientReceiptEvent {
    data class Invoice(val payment: PaymentUiModel) : Button
    data class Share(val payment: PaymentUiModel) : Button
  }

  sealed interface GoTo : ClientReceiptEvent {
    data object BackHandler : GoTo
  }
}
