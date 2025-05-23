package net.techandgraphics.wastemanagement.ui.screen.invoice

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

data class InvoiceState(
  val invoices: List<PaymentUiModel> = listOf(),
)
