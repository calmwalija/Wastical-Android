package net.techandgraphics.wastemanagement.ui.screen.client.invoice

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.MainActivityState

data class ClientInvoiceState(
  val invoices: List<PaymentUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
)
