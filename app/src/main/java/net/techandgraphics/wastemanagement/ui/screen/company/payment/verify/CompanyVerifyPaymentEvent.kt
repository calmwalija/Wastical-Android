package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface CompanyVerifyPaymentEvent {

  data object Load : CompanyVerifyPaymentEvent

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

  sealed interface Goto : CompanyVerifyPaymentEvent {
    data object BackHandler : Goto
    data class Profile(val id: Long) : Goto
  }
}
