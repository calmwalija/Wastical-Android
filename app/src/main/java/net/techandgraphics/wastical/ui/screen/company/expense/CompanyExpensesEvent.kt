package net.techandgraphics.wastical.ui.screen.company.expense

import net.techandgraphics.wastical.data.local.database.expense.ExpenseCategory
import net.techandgraphics.wastical.data.local.database.expense.ExpensePeriod

sealed interface CompanyExpensesEvent {
  data object Load : CompanyExpensesEvent
  data class Add(
    val title: String,
    val amount: Long,
    val category: ExpenseCategory,
    val period: ExpensePeriod,
    val intervalDays: Int? = null,
    val quantity: Int? = null,
    val dateEpochSeconds: Long,
  ) : CompanyExpensesEvent
}
