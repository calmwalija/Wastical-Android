package net.techandgraphics.wastical.ui.screen.company.client.history

import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

sealed interface CompanyPaymentHistoryEvent {

  data class Load(val id: Long) : CompanyPaymentHistoryEvent

  sealed interface Button : CompanyPaymentHistoryEvent {
    sealed interface Invoice : Button {
      data class Event(val payment: PaymentUiModel, val op: Op) : Invoice
      enum class Op { Preview, Share }
    }

    data class Delete(val id: Long) : Button
    data class Phone(val contact: String) : Button
  }

  sealed interface Payment : CompanyPaymentHistoryEvent {
    data class Approve(val payment: PaymentUiModel) : Payment
    data class Deny(val payment: PaymentUiModel) : Payment
  }

  sealed interface Goto : CompanyPaymentHistoryEvent {
    data object BackHandler : Goto
    data class Location(val id: Long) : Goto
  }
}
