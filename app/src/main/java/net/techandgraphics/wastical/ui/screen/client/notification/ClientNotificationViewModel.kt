package net.techandgraphics.wastical.ui.screen.client.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toNotificationUiModel
import javax.inject.Inject

@HiltViewModel
class ClientNotificationViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientNotificationState>(ClientNotificationState.Loading)
  val state = _state.asStateFlow()
  private var searchJob: Job? = null

  init {
    onEvent(ClientNotificationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = ClientNotificationState.Success(company = company)
    flowOf()
  }

  private suspend fun flowOf(query: String = "") {
    if (_state.value is ClientNotificationState.Success) {
      val state = (_state.value as ClientNotificationState.Success)
      database.notificationDao.flowOf(
        query = query,
        sort = state.sort,
      ).map { p0 -> p0.map { it.toNotificationUiModel() } }
        .collectLatest { notifications ->
          _state.value = state.copy(notifications = notifications)
        }
    }
  }

  private fun onQuery(event: ClientNotificationEvent.Input.Query) {
    if (_state.value is ClientNotificationState.Success) {
      val state = (_state.value as ClientNotificationState.Success)
      _state.value = state.copy(query = event.query)
      searchJob?.cancel()
      searchJob = viewModelScope.launch {
        delay(500)
        flowOf(event.query.trim())
      }
    }
  }

  private fun sortBy(event: ClientNotificationEvent.Button.Sort) =
    viewModelScope.launch {
      if (_state.value is ClientNotificationState.Success) {
        val state = (_state.value as ClientNotificationState.Success)
        _state.value = state.copy(sort = event.sort)
        flowOf(state.query)
      }
    }

  fun onEvent(event: ClientNotificationEvent) {
    when (event) {
      ClientNotificationEvent.Load -> onLoad()
      is ClientNotificationEvent.Button.Sort -> sortBy(event)
      is ClientNotificationEvent.Input.Query -> onQuery(event)
      else -> Unit
    }
  }
}
