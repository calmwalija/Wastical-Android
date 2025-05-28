package net.techandgraphics.wastemanagement.ui.screen.client.home

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface ClientHomeEvent {

  data class AppState(val state: MainActivityState) : ClientHomeEvent

  sealed interface Button : ClientHomeEvent {
    sealed interface Payment : Button {
      data class Invoice(val payment: PaymentUiModel) : Payment
      data class Share(val payment: PaymentUiModel) : Payment
    }

    data object MakePayment : Button
    data object WasteSortGuide : Button
  }

  sealed interface Goto : ClientHomeEvent {
    data object Invoice : Goto
  }
}
