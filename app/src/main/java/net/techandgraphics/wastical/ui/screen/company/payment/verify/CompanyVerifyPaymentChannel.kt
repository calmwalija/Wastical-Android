package net.techandgraphics.wastical.ui.screen.company.payment.verify

import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface CompanyVerifyPaymentChannel {
  sealed interface Payment : CompanyVerifyPaymentChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
