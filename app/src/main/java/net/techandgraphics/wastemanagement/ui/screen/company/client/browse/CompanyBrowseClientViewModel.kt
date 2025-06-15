package net.techandgraphics.wastemanagement.ui.screen.company.client.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toSearchTagEntity
import net.techandgraphics.wastemanagement.domain.model.search.SearchTagUiModel
import net.techandgraphics.wastemanagement.domain.toAreaUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toSearchTagUiModel
import net.techandgraphics.wastemanagement.domain.toStreetUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyBrowseClientViewModel @Inject constructor(
  private val database: AppDatabase,
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

  private suspend fun onLoad() {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val demographicAreas = database.demographicAreaDao.query().map { it.toAreaUiModel() }
    val demographicStreets = database.demographicStreetDao.query().map { it.toStreetUiModel() }
    combine(
      database.accountDao.qAccountData(),
      database.searchTagDao.query(),
    ) { accounts, tags ->
      _state.value = CompanyBrowseClientState.Success(
        accounts = accounts,
        company = company,
        searchHistoryTags = tags.map { it.toSearchTagUiModel() },
        demographicAreas = demographicAreas,
        demographicStreets = demographicStreets,
      )
    }
      .launchIn(viewModelScope)
  }

  private suspend fun onQueryChange() {
    val currentState = _state.value
    if (currentState is CompanyBrowseClientState.Success) {
      val ids = if (currentState.filters.isEmpty()) {
        null
      } else {
        currentState.filters
      }
      database.accountDao
        .qAccountData(query = currentState.query.trim(), ids)
        .collectLatest {
          _state.value = currentState.copy(accounts = it)
        }
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
        val ids = if (newState.filters.isEmpty()) null else newState.filters
        database.accountDao.qAccountData(
          query = newState.query.trim(),
          ids = ids,
        ).collectLatest { accounts ->
          val latestState = _state.value
          if (latestState is CompanyBrowseClientState.Success) {
            _state.value = latestState.copy(accounts = accounts)
          }
        }
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

      else -> Unit
    }
  }
}
