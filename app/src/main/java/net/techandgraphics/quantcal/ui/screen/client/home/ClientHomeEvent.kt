package net.techandgraphics.quantcal.ui.screen.client.home

import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel

sealed interface ClientHomeEvent {

  data class Load(val id: Long) : ClientHomeEvent

  sealed interface Button : ClientHomeEvent {
    data object Fetch : Button
    sealed interface Payment : Button {
      data class Invoice(val payment: PaymentUiModel) : Payment
      data class Share(val payment: PaymentUiModel) : Payment
      data class TextToClipboard(val text: String) : Payment
    }

    data object MakePayment : Button
    data object WasteSortGuide : Button
  }

  sealed interface Goto : ClientHomeEvent {
    data object Invoice : Goto
  }
}
