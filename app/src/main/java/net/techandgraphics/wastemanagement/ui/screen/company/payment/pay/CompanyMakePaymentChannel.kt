package net.techandgraphics.wastemanagement.ui.screen.company.payment.pay

sealed interface CompanyMakePaymentChannel {

  sealed interface Pay : CompanyMakePaymentChannel {
    data object Success : Pay
  }
}
