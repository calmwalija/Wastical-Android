package net.techandgraphics.wastical.ui.screen.company.payment.pay

sealed interface CompanyMakePaymentChannel {

  sealed interface Pay : CompanyMakePaymentChannel {
    data object Success : Pay
  }
}
