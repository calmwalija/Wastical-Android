package net.techandgraphics.wastical.ui.screen.company.payment.verify

import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

sealed interface CompanyVerifyPaymentEvent {

  data object Load : CompanyVerifyPaymentEvent

  sealed interface Payment : CompanyVerifyPaymentEvent {
    data class Approve(val payment: PaymentUiModel) : Payment
    data class Deny(val payment: PaymentUiModel) : Payment
    data class Image(val payment: PaymentUiModel) : Payment
    data class Download(val payment: PaymentUiModel) : Payment
  }

  sealed interface Goto : CompanyVerifyPaymentEvent {
    data object BackHandler : Goto
    data class Profile(val id: Long) : Goto
  }

  sealed interface Input : CompanyVerifyPaymentEvent {
    class Search(val query: String) : CompanyVerifyPaymentEvent
  }
  sealed interface Button : CompanyVerifyPaymentEvent {
    data object Clear : Button
  }
}
