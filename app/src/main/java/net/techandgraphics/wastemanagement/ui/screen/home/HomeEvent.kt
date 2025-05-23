package net.techandgraphics.wastemanagement.ui.screen.home

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface HomeEvent {

  sealed interface Button : HomeEvent {
    sealed interface Payment : Button {
      data class Tap(val payment: PaymentUiModel) : Payment
      data class Share(val payment: PaymentUiModel) : Payment
    }
  }
}
