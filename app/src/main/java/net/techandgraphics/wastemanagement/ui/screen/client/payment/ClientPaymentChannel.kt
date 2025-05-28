package net.techandgraphics.wastemanagement.ui.screen.client.payment

import net.techandgraphics.wastemanagement.data.remote.LoadingEvent

sealed interface ClientPaymentChannel {

  sealed interface Pay : ClientPaymentChannel {
    data object Success : Pay
    data class Failure(val errorHandler: LoadingEvent.Error) : Pay
  }
}
