package net.techandgraphics.wastemanagement.ui.screen.client.home

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface HomeEvent {

  data class AppState(val state: MainActivityState) : HomeEvent

  sealed interface Button : HomeEvent {
    sealed interface Payment : Button {
      data class Invoice(val payment: PaymentUiModel) : Payment
      data class Share(val payment: PaymentUiModel) : Payment
    }

    data object MakePayment : Button
    data object WasteSortGuide : Button
  }

  sealed interface Goto : HomeEvent {
    data object Invoice : Goto
  }
}
