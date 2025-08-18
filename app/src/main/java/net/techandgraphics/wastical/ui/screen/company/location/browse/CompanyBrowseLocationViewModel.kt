package net.techandgraphics.wastical.ui.screen.company.location.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.getToday
import javax.inject.Inject

@HiltViewModel
class CompanyBrowseLocationViewModel @Inject constructor(
  private val database: AppDatabase,
  private val preferences: Preferences,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyBrowseLocationState>(CompanyBrowseLocationState.Loading)
  val state = _state.asStateFlow()
  private var searchJob: Job? = null

  init {
    onEvent(CompanyBrowseLocationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val (_, month, year) = getToday()
    val default = Gson().toJson(MonthYear(month, year))
    preferences.flowOf<String>(Preferences.CURRENT_WORKING_MONTH, default)
      .collectLatest { jsonString ->
        val monthYear = Gson().fromJson(jsonString, MonthYear::class.java)
        database.paymentDao.qPayment4CurrentLocationMonth(monthYear.month, monthYear.year)
          .collectLatest { payment4CurrentLocationMonth ->
            val company = database.companyDao.query().first().toCompanyUiModel()
            _state.value = CompanyBrowseLocationState.Success(
              payment4CurrentLocationMonth = payment4CurrentLocationMonth,
              company = company,
              monthYear = monthYear,
            )
          }
      }
  }

  private fun onSearch(event: CompanyBrowseLocationEvent.Input.Search) {
    _state.value =
      (_state.value as CompanyBrowseLocationState.Success).copy(query = event.query)
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      delay(1_000)
      onQueryChange()
    }
  }

  private suspend fun onQueryChange() {
    if (_state.value is CompanyBrowseLocationState.Success) {
      val state = (_state.value as CompanyBrowseLocationState.Success)
      val currentState = (_state.value as CompanyBrowseLocationState.Success)

      database.paymentDao.qPayment4CurrentLocationMonth(
        state.monthYear.month,
        state.monthYear.year,
        query = currentState.query.trim(),
      )
        .collectLatest { payment4CurrentLocationMonth ->
          val sorted = sort(payment4CurrentLocationMonth, currentState.sortBy)
          _state.value = (_state.value as CompanyBrowseLocationState.Success).copy(
            payment4CurrentLocationMonth = sorted,
          )
        }
    }
  }

  private fun clearSearchQuery() {
    onSearch(CompanyBrowseLocationEvent.Input.Search(""))
  }

  fun onEvent(event: CompanyBrowseLocationEvent) {
    when (event) {
      CompanyBrowseLocationEvent.Load -> onLoad()
      is CompanyBrowseLocationEvent.Input.Search -> onSearch(event)
      is CompanyBrowseLocationEvent.Button.Clear -> clearSearchQuery()
      is CompanyBrowseLocationEvent.SortBy -> onSortBy(event)
      else -> Unit
    }
  }

  private fun onSortBy(event: CompanyBrowseLocationEvent.SortBy) = viewModelScope.launch {
    val current = _state.value
    if (current is CompanyBrowseLocationState.Success) {
      val sorted = sort(current.payment4CurrentLocationMonth, event.sort)
      _state.value = current.copy(sortBy = event.sort, payment4CurrentLocationMonth = sorted)
    }
  }

  private fun sort(
    list: List<Payment4CurrentLocationMonth>,
    order: LocationSortOrder,
  ): List<Payment4CurrentLocationMonth> {
    return when (order) {
      LocationSortOrder.NameAsc -> list.sortedBy { it.streetName.lowercase() }
      LocationSortOrder.CompletedPayDesc -> list.sortedByDescending { safeCompletion(it) }
      LocationSortOrder.CompletedPayAsc -> list.sortedBy { safeCompletion(it) }
    }
  }

  private fun safeCompletion(item: Payment4CurrentLocationMonth): Float {
    val total = item.totalAccounts
    if (total <= 0) return 0f
    return item.paidAccounts.toFloat() / total.toFloat()
  }
}
