package net.techandgraphics.wastemanagement.ui.screen.auth.phone.opt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class OptViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow<OptState>(OptState.Loading)
  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = OptState.Loading,
    )

  private fun onLoad(event: OptEvent.Load) {
    _state.value = OptState.Success(phone = event.phone)
  }

  fun onEvent(event: OptEvent) {
    when (event) {
      is OptEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
