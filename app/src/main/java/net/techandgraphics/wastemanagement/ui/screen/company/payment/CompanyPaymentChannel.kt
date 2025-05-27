package net.techandgraphics.wastemanagement.ui.screen.company.payment

import net.techandgraphics.wastemanagement.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastemanagement.data.remote.LoadingEvent

sealed interface CompanyPaymentChannel {
  sealed interface Payment : CompanyPaymentChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: LoadingEvent.Error) : Payment
  }
}
