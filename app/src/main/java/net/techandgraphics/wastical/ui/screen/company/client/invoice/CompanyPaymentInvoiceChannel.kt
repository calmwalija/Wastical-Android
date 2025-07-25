package net.techandgraphics.wastical.ui.screen.company.client.invoice

import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface CompanyPaymentInvoiceChannel {
  sealed interface Payment : CompanyPaymentInvoiceChannel {
    data class Success(val payments: List<PaymentEntity>) : Payment
    data class Failure(val error: ApiResult.Error) : Payment
  }
}
