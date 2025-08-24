package net.techandgraphics.wastical.ui.screen.company.client.browse

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toSearchTagEntity
import net.techandgraphics.wastical.domain.model.search.SearchTagUiModel
import net.techandgraphics.wastical.domain.toAccountRequestUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toDemographicAreaUiModel
import net.techandgraphics.wastical.domain.toDemographicStreetUiModel
import net.techandgraphics.wastical.domain.toSearchTagUiModel
import net.techandgraphics.wastical.worker.company.payment.scheduleCompanyPaymentRequestWorker
import javax.inject.Inject

@HiltViewModel
class CompanyBrowseClientViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyBrowseClientState>(CompanyBrowseClientState.Loading)

  private val _channel = Channel<CompanyBrowseClientChannel>()
  val channel = _channel.receiveAsFlow()
  private var searchJob: Job? = null

  val state = _state
    .onStart {
      viewModelScope.launch {
        launch { onLoad() }
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyBrowseClientState.Loading,
    )

  private fun onSearch(event: CompanyBrowseClientListEvent.Input.Search) {
    _state.value = (_state.value as CompanyBrowseClientState.Success).copy(query = event.query)
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      delay(1_000)
      onQueryChange()
    }
  }

  private fun flowOfPaging(query: String = "", ids: Set<Long>? = null) {
    if (_state.value is CompanyBrowseClientState.Success) {
      val state = (_state.value as CompanyBrowseClientState.Success)
      Pager(
        config = PagingConfig(
          pageSize = 40,
          initialLoadSize = 120,
          prefetchDistance = 10,
          maxSize = 200,
          enablePlaceholders = false,
        ),
        pagingSourceFactory = {
          database.accountDao.flowOfPaging(
            query = query,
            ids = ids,
          )
        },
      ).flow
        .flowOn(Dispatchers.Default)
        .cachedIn(viewModelScope)
        .also { flowOfPayments ->
          _state.value = state.copy(accounts = flowOfPayments)
        }
    }
  }

  private suspend fun onLoad() {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val demographicAreas = database.demographicAreaDao.query().map { it.toDemographicAreaUiModel() }
    val demographicStreets =
      database.demographicStreetDao.query().map { it.toDemographicStreetUiModel() }
    combine(
      database.searchTagDao.query(),
      database.accountRequestDao.flowOf()
        .map { dataOf -> dataOf.map { it.toAccountRequestUiModel() } },
    ) { tags, accountRequests ->
      _state.value = CompanyBrowseClientState.Success(
        company = company,
        searchHistoryTags = tags.map { it.toSearchTagUiModel() },
        demographicAreas = demographicAreas,
        demographicStreets = demographicStreets,
        accountRequests = accountRequests,
      )
      flowOfPaging()
    }
      .launchIn(viewModelScope)
  }

  private fun onQueryChange() {
    val currentState = _state.value
    if (currentState is CompanyBrowseClientState.Success) {
      val ids = currentState.filters.ifEmpty { null }
      flowOfPaging(ids = ids, query = currentState.query)
    }
  }

  private fun onFilterBy(event: CompanyBrowseClientListEvent.Button.FilterBy) =
    viewModelScope.launch {
      val currentState = _state.value
      if (currentState is CompanyBrowseClientState.Success) {
        val updatedFilters = currentState.filters.toMutableSet().apply {
          if (contains(event.id)) remove(event.id) else add(event.id)
        }
        _state.value = currentState.copy(filters = updatedFilters)
        val newState = _state.value as CompanyBrowseClientState.Success
        val ids = newState.filters.ifEmpty { null }
        flowOfPaging(ids = ids, query = currentState.query)
      }
    }

  private fun onHistoryTag() = viewModelScope.launch {
    val currentState = _state.value
    if (currentState is CompanyBrowseClientState.Success) {
      val query = currentState.query.trim().ifEmpty { return@launch }
      SearchTagUiModel(query = query, tag = query.replace(" ", "").lowercase()).toSearchTagEntity()
        .also { database.searchTagDao.insert(it) }
    }
  }

  private fun clearSearchQuery() {
    onSearch(CompanyBrowseClientListEvent.Input.Search(""))
  }

  private fun onButtonTag(event: CompanyBrowseClientListEvent.Button.Tag) {
    clearSearchQuery()
    onSearch(CompanyBrowseClientListEvent.Input.Search(event.tag.query))
  }

  fun onEvent(event: CompanyBrowseClientListEvent) {
    when (event) {
      is CompanyBrowseClientListEvent.Input.Search -> onSearch(event)
      CompanyBrowseClientListEvent.Button.Clear -> clearSearchQuery()
      CompanyBrowseClientListEvent.Button.HistoryTag -> onHistoryTag()
      is CompanyBrowseClientListEvent.Button.Tag -> onButtonTag(event)
      is CompanyBrowseClientListEvent.Button.FilterBy -> onFilterBy(event)
      CompanyBrowseClientListEvent.Button.ScheduleUpload -> application.scheduleCompanyPaymentRequestWorker()

      else -> Unit
    }
  }
}
