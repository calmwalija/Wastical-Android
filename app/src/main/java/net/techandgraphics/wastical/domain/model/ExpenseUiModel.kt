package net.techandgraphics.wastical.domain.model

data class ExpenseUiModel(
  val id: Long = 0,
  val title: String,
  val amount: Long,
  val category: String,
  val period: String,
  val intervalDays: Int?,
  val quantity: Int?,
  val expenseDate: Long,
  val companyId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
