package net.techandgraphics.quantcal.ui.screen.client.home

import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel

sealed interface ClientHomeEvent {

  data object Load : ClientHomeEvent

  sealed interface Button : ClientHomeEvent {
    data object Fetch : Button
    data object Logout : Button
    sealed interface Payment : Button {
      data class Invoice(val payment: PaymentUiModel) : Payment
      data class Share(val payment: PaymentUiModel) : Payment
      data class TextToClipboard(val text: String) : Payment
    }

    data class MakePayment(val id: Long) : Button
    data object WasteSortGuide : Button
  }

  sealed interface Goto : ClientHomeEvent {
    data class Invoice(val id: Long) : Goto
    data object Login : Goto
  }
}
