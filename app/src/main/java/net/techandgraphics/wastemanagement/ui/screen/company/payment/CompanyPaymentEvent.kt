package net.techandgraphics.wastemanagement.ui.screen.company.payment

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyPaymentEvent {
  data class AppState(val state: MainActivityState) : CompanyPaymentEvent

  sealed interface Payment : CompanyPaymentEvent {
    sealed interface Button : Payment {

      data class Status(val payment: PaymentUiModel, val status: PaymentStatus) : Button
    }
  }
}
