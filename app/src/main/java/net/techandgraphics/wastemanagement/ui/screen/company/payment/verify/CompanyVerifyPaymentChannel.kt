package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.LoadingEvent

sealed interface CompanyVerifyPaymentChannel {
  sealed interface Payment : CompanyVerifyPaymentChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: LoadingEvent.Error) : Payment
  }
}
