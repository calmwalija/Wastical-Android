package net.techandgraphics.quantcal.ui.screen.client.invoice

import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.ui.activity.MainActivityState

data class ClientInvoiceState(
  val invoices: List<PaymentUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
)
