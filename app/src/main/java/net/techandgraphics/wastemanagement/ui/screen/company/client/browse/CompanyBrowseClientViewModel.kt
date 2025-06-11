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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.Pattern
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toAccountWithStreetAndAreaUiModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class CompanyBrowseClientViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow(CompanyBrowseClientState())

  private val _channel = Channel<CompanyBrowseClientChannel>()
  val channel = _channel.receiveAsFlow()
  private var searchJob: Job? = null

  private fun getByMonth() = viewModelScope.launch {
    val ago4hour = ZonedDateTime
      .now()
      .minusHours(3)
      .format(DateTimeFormatter.ofPattern(Pattern.DATE_YYYY_MM))
    val results = database.accountDao.getByCreatedAt(ago4hour)
  }

  val state = _state
    .onStart {
      viewModelScope.launch {
        launch { onQueryChange() }
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyBrowseClientState(),
    )

  private fun onSearch(event: CompanyBrowseClientListEvent.Input.Search) {
    _state.update { it.copy(query = event.query) }
    searchJob?.cancel()
    searchJob = viewModelScope.launch {
      delay(5_00)
      onQueryChange()
    }
  }

  private suspend fun onQueryChange() = database.accountDao
    .qAccountWithStreetAndArea(_state.value.query.trim())
    .map { it.map { it.toAccountWithStreetAndAreaUiModel() } }
    .collectLatest { _state.value = _state.value.copy(accounts = it) }

  fun onEvent(event: CompanyBrowseClientListEvent) {
    when (event) {
      is CompanyBrowseClientListEvent.Input.Search -> onSearch(event)
      CompanyBrowseClientListEvent.Button.Clear -> onSearch(
        CompanyBrowseClientListEvent.Input.Search(
          "",
        ),
      )

      else -> Unit
    }
  }
}
