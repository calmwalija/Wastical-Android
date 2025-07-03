package net.techandgraphics.quantcal.ui.screen.company.payment.verify

import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface CompanyVerifyPaymentChannel {
  sealed interface Payment : CompanyVerifyPaymentChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
