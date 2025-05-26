package net.techandgraphics.wastemanagement.ui.screen.client.payment

import net.techandgraphics.wastemanagement.data.remote.LoadingEvent

sealed interface PaymentChannel {

  sealed interface Pay : PaymentChannel {
    data object Success : Pay
    data class Failure(val errorHandler: LoadingEvent.Error) : Pay
  }
}
