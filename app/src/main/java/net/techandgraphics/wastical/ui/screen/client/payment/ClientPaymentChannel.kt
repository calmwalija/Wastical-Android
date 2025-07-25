package net.techandgraphics.wastical.ui.screen.client.payment

sealed interface ClientPaymentChannel {

  sealed interface Pay : ClientPaymentChannel {
    data object Success : Pay
  }
}
