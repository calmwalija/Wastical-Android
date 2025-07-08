package net.techandgraphics.quantcal.ui.screen.company.client.history

import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface CompanyPaymentHistoryChannel {
  sealed interface Payment : CompanyPaymentHistoryChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
