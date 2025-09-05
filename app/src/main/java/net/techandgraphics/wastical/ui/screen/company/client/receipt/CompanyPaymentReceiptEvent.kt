package net.techandgraphics.wastical.ui.screen.company.client.receipt

import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

sealed interface CompanyPaymentReceiptEvent {

  data class Load(val id: Long) : CompanyPaymentReceiptEvent

  sealed interface Button : CompanyPaymentReceiptEvent {
    sealed interface Invoice : Button {
      data class Event(val payment: PaymentUiModel, val op: Op) : Invoice
      enum class Op { Preview, Share }
    }

    data class Phone(val contact: String) : Button
  }

  sealed interface Goto : CompanyPaymentReceiptEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }
}
