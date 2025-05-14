package net.techandgraphics.wastemanagement.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(HomeState())
  private val _channel = Channel<HomeChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = HomeState(),
    )

  fun onEvent(event: HomeEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
