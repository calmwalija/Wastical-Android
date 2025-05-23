package net.techandgraphics.wastemanagement.ui.screen.transaction

import net.techandgraphics.wastemanagement.ui.screen.home.model.TransactionUiModel

data class TransactionState(
  val id: Int = 0,
  val transactionUiModels: List<TransactionUiModel> = emptyList(),
)
