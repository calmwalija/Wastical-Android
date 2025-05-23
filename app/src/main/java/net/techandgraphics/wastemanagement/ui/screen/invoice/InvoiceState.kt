package net.techandgraphics.wastemanagement.ui.screen.invoice

import net.techandgraphics.wastemanagement.ui.screen.home.model.TransactionUiModel

data class InvoiceState(
  val id: Int = 0,
  val transactionUiModels: List<TransactionUiModel> = emptyList(),
)
