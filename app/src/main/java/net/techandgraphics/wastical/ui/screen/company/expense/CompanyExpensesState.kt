package net.techandgraphics.wastical.ui.screen.company.expense

import net.techandgraphics.wastical.domain.model.ExpenseUiModel

sealed interface CompanyExpensesState {
  data object Loading : CompanyExpensesState
  data class Success(
    val expenses: List<ExpenseUiModel>,
    val collected: Long,
    val totalExpenses: Long,
    val profit: Long,
  ) : CompanyExpensesState
}
