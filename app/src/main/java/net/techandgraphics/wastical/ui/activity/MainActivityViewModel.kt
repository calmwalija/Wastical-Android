package net.techandgraphics.wastical.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.Preferences
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  private val preferences: Preferences,
) : ViewModel() {

  private val _state = MutableStateFlow(MainActivityState())
  val state = _state.asStateFlow()

  init {
    onEvent(MainActivityEvent.Load)
  }

  private fun onLoad() {
    viewModelScope.launch {
      preferences.flowOf<Boolean>(Preferences.DYNAMIC_COLOR, false)
        .collectLatest { dynamicColor ->
          _state.update { it.copy(dynamicColor = dynamicColor) }
        }
    }
  }

  fun onEvent(event: MainActivityEvent) {
    when (event) {
      MainActivityEvent.Load -> onLoad()
    }
  }
}
