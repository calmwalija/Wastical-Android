package net.techandgraphics.wastical.ui.screen.company.payment.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentTimelineViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentTimelineState>(CompanyPaymentTimelineState.Loading)
  private var searchJob: Job? = null

  val state = _state.asStateFlow()

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = CompanyPaymentTimelineState.Success(company = company)
    flowOfPaging()
  }

  private fun flowOfPaging(query: String = "") {
    if (_state.value is CompanyPaymentTimelineState.Success) {
      val state = (_state.value as CompanyPaymentTimelineState.Success)
      val pagingSourceFactory = database.paymentDao.flowOfPaging(
        query = query,
        sort = state.sort,
      )
      Pager(
        config = PagingConfig(
          pageSize = 20,
          initialLoadSize = 40,
          prefetchDistance = 10,
        ),
        pagingSourceFactory = { pagingSourceFactory },
      ).flow
        .map { p0 -> p0.map { it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel() } }
        .also { flowOfPayments ->
          _state.value = state.copy(payments = flowOfPayments)
        }
    }
  }

  fun onEvent(event: CompanyPaymentTimelineEvent) {
    when (event) {
      CompanyPaymentTimelineEvent.Load -> onLoad()
      is CompanyPaymentTimelineEvent.Input.Query -> onInputQuery(event)
      is CompanyPaymentTimelineEvent.Button.Sort -> onButtonSort(event)
      else -> Unit
    }
  }

  private fun onInputQuery(event: CompanyPaymentTimelineEvent.Input.Query) {
    if (_state.value is CompanyPaymentTimelineState.Success) {
      val state = (_state.value as CompanyPaymentTimelineState.Success)
      _state.value = state.copy(query = event.query)
      searchJob?.cancel()
      searchJob = viewModelScope.launch {
        delay(500)
        flowOfPaging(event.query.trim())
      }
    }
  }

  private fun onButtonSort(event: CompanyPaymentTimelineEvent.Button.Sort) {
    if (_state.value is CompanyPaymentTimelineState.Success) {
      val state = (_state.value as CompanyPaymentTimelineState.Success)
      _state.value = state.copy(sort = event.value)
      flowOfPaging(state.query)
    }
  }
}
