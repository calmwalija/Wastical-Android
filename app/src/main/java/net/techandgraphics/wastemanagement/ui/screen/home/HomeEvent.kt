package net.techandgraphics.wastemanagement.ui.screen.home

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface HomeEvent {
  data class Invoice(val payment: PaymentUiModel) : HomeEvent
}
