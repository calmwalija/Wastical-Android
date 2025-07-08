package net.techandgraphics.quantcal.ui.screen.company.client.invoice

import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.ui.activity.MainActivityState

sealed interface CompanyPaymentInvoiceEvent {

  data class Load(val id: Long, val state: MainActivityState) : CompanyPaymentInvoiceEvent

  sealed interface Button : CompanyPaymentInvoiceEvent {
    sealed interface Invoice : Button {
      data class Event(val payment: PaymentUiModel, val op: Op) : Invoice
      enum class Op { Preview, Share }
    }

    data class Phone(val contact: String) : Button
  }

  sealed interface Goto : CompanyPaymentInvoiceEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }
}
