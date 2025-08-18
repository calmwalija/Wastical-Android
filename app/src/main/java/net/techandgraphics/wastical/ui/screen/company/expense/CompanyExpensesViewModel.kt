package net.techandgraphics.wastical.ui.screen.company.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.expense.ExpenseEntity
import net.techandgraphics.wastical.domain.toExpenseUiModel
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyExpensesViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyExpensesState>(CompanyExpensesState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(CompanyExpensesEvent.Load)
  }

  fun onEvent(event: CompanyExpensesEvent) {
    when (event) {
      CompanyExpensesEvent.Load -> onLoad()
      is CompanyExpensesEvent.Add -> onAdd(event)
    }
  }

  private fun monthBounds(now: ZonedDateTime = ZonedDateTime.now()): Pair<Long, Long> {
    val start = now.withDayOfMonth(1).toEpochSecond()
    val end = now.withDayOfMonth(now.toLocalDate().lengthOfMonth()).toEpochSecond()
    return start to end
  }

  private fun onLoad() = viewModelScope.launch {
    val (start, end) = monthBounds()
    combine(
      database.expenseDao.flowOfRange(start, end).map { list -> list.map { it.toExpenseUiModel() } },
      database.paymentDao.sumFeeSince(status = "Approved", since = start),
      database.expenseDao.sumInRange(start, end),
    ) { expenses, collected, expensesSum ->
      _state.value = CompanyExpensesState.Success(
        expenses = expenses,
        collected = collected,
        totalExpenses = expensesSum,
        profit = collected - expensesSum,
      )
    }.launchIn(viewModelScope)
  }

  private fun onAdd(event: CompanyExpensesEvent.Add) = viewModelScope.launch {
    val now = System.currentTimeMillis() / 1000
    val entity = ExpenseEntity(
      title = event.title,
      amount = if (event.category.name == "Salary" && (event.quantity ?: 1) > 1) {
        event.amount * (event.quantity ?: 1)
      } else {
        event.amount
      },
      category = event.category.name,
      period = event.period.name,
      intervalDays = event.intervalDays,
      quantity = event.quantity,
      expenseDate = event.dateEpochSeconds,
      companyId = 1L,
      createdAt = now,
      updatedAt = now,
    )
    database.expenseDao.upsert(entity)
  }
}
