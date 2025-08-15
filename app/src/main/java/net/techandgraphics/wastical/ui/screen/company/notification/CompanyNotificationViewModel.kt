package net.techandgraphics.wastical.ui.screen.company.notification

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
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toNotificationUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyNotificationViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyNotificationState>(CompanyNotificationState.Loading)
  val state = _state.asStateFlow()
  private var searchJob: Job? = null

  init {
    onEvent(CompanyNotificationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = CompanyNotificationState.Success(company = company)
    flowOfPaging()
  }

  private fun flowOfPaging(query: String = "") {
    if (_state.value is CompanyNotificationState.Success) {
      val state = (_state.value as CompanyNotificationState.Success)
      database.notificationDao.flowOfPaging(
        query = query,
        sort = state.sort,
      ).also { pagingSource ->
        Pager(
          config = PagingConfig(
            pageSize = 20,
            initialLoadSize = 40,
            prefetchDistance = 10,
          ),
          pagingSourceFactory = { pagingSource },
        ).flow
          .map { p0 -> p0.map { it.toNotificationUiModel() } }
          .also { flowOf ->
            _state.value = state.copy(notifications = flowOf)
          }
      }
    }
  }

  private fun onQuery(event: CompanyNotificationEvent.Input.Query) {
    if (_state.value is CompanyNotificationState.Success) {
      val state = (_state.value as CompanyNotificationState.Success)
      _state.value = state.copy(query = event.query)
      searchJob?.cancel()
      searchJob = viewModelScope.launch {
        delay(500)
        flowOfPaging(event.query.trim())
      }
    }
  }

  private fun sortBy(event: CompanyNotificationEvent.Button.Sort) {
    if (_state.value is CompanyNotificationState.Success) {
      val state = (_state.value as CompanyNotificationState.Success)
      _state.value = state.copy(sort = event.sort)
      flowOfPaging(state.query)
    }
  }

  fun onEvent(event: CompanyNotificationEvent) {
    when (event) {
      CompanyNotificationEvent.Load -> onLoad()
      is CompanyNotificationEvent.Input.Query -> onQuery(event)
      is CompanyNotificationEvent.Button.Sort -> sortBy(event)
      else -> Unit
    }
  }
}
