package net.techandgraphics.wastemanagement.ui.screen.company.client.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CompanyListClientViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(CompanyListClientState())

  private val _channel = Channel<CompanyListClientChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyListClientState(),
    )

  private fun onAppState(event: CompanyListClientEvent.AppState) {
    _state.update { it.copy(state = event.state) }
  }

  fun onEvent(event: CompanyListClientEvent) {
    when (event) {
      is CompanyListClientEvent.AppState -> onAppState(event)
      else -> TODO("Handle actions")
    }
  }
}
