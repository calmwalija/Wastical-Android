package net.techandgraphics.wastical.ui.screen.company.payment.verify

import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel

sealed interface CompanyVerifyPaymentEvent {

  data class Load(val ofType: String) : CompanyVerifyPaymentEvent

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

  sealed interface Button : CompanyVerifyPaymentEvent {
    data class Status(val status: PaymentStatus) : Button
  }
}
