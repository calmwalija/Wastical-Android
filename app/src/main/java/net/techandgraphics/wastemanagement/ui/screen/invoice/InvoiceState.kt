package net.techandgraphics.wastemanagement.ui.screen.invoice

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class InvoiceState(
  val invoices: List<PaymentUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
)
