package net.techandgraphics.quantcal.ui.screen.client.payment

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface ClientPaymentChannel {

  sealed interface Pay : ClientPaymentChannel {
    data object Success : Pay
    data class Failure(val error: ApiResult.Error) : Pay
  }
}
