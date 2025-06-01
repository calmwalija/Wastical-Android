package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyVerifyPaymentEvent {
  data class AppState(val state: MainActivityState) : CompanyVerifyPaymentEvent

  sealed interface Payment : CompanyVerifyPaymentEvent {
    sealed interface Button : Payment {
      data class Status(val payment: PaymentUiModel, val status: PaymentStatus) : Button
    }
  }

  sealed interface Verify : CompanyVerifyPaymentEvent {
    sealed interface Button : Verify {
      data class Status(val status: PaymentStatus) : Button
    }
  }
}
