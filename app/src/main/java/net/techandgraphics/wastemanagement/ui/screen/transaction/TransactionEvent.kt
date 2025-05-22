package net.techandgraphics.wastemanagement.ui.screen.transaction

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface TransactionEvent {
  sealed interface Button : TransactionEvent {
    data class Tap(val payment: PaymentUiModel) : Button
    data class Share(val payment: PaymentUiModel) : Button
  }
}
