package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

import net.techandgraphics.wastemanagement.data.remote.ApiResult

sealed interface CompanyMakePaymentChannel {

  sealed interface Pay : CompanyMakePaymentChannel {
    data object Success : Pay
    data class Failure(val error: ApiResult.Error) : Pay
  }
}
