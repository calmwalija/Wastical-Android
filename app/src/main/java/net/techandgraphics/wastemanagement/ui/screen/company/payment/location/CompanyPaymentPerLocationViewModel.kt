package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.getToday
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentPerLocationViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentPerLocationState>(CompanyPaymentPerLocationState.Loading)
  val state = _state.asStateFlow()
  private var searchJob: Job? = null

  init {
    onEvent(CompanyPaymentPerLocationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val (_, month, year) = getToday()
    database.paymentDao.qPayment4CurrentLocationMonth(month, year)
      .collectLatest { payment4CurrentLocationMonth ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        _state.value = CompanyPaymentPerLocationState.Success(
          payment4CurrentLocationMonth = payment4CurrentLocationMonth,
          company = company,
        )
      }
  }

  private fun onSearch(event: CompanyPaymentPerLocationEvent.Input.Search) {
    _state.value =
      (_state.value as CompanyPaymentPerLocationState.Success).copy(query = event.query)
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      delay(1_000)
      onQueryChange()
    }
  }

  private suspend fun onQueryChange() {
    if (_state.value is CompanyPaymentPerLocationState.Success) {
      val (_, month, year) = getToday()
      val currentState = (_state.value as CompanyPaymentPerLocationState.Success)

      database.paymentDao.qPayment4CurrentLocationMonth(
        month,
        year,
        query = currentState.query.trim(),
      )
        .collectLatest { payment4CurrentLocationMonth ->
          _state.value = (_state.value as CompanyPaymentPerLocationState.Success).copy(
            payment4CurrentLocationMonth = payment4CurrentLocationMonth,
          )
        }
    }
  }

  private fun clearSearchQuery() {
    onSearch(CompanyPaymentPerLocationEvent.Input.Search(""))
  }

  fun onEvent(event: CompanyPaymentPerLocationEvent) {
    when (event) {
      CompanyPaymentPerLocationEvent.Load -> onLoad()
      is CompanyPaymentPerLocationEvent.Input.Search -> onSearch(event)
      is CompanyPaymentPerLocationEvent.Button.Clear -> clearSearchQuery()
      else -> Unit
    }
  }
}
