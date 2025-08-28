package net.techandgraphics.wastical.ui.activity

import android.app.Application
import android.content.res.Configuration
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
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(MainActivityState())
  val state = _state.asStateFlow()

  init {
    onEvent(MainActivityEvent.Load)
  }

  private fun onLoad() {
    viewModelScope.launch {
      kotlinx.coroutines.flow.combine(
        preferences.existsBoolean(Preferences.DARK_THEME),
        preferences.flowOf<Boolean>(Preferences.DARK_THEME, false),
        preferences.flowOf<Boolean>(Preferences.DYNAMIC_COLOR, false),
      ) { hasDarkPref, darkTheme, dynamicColor -> Triple(hasDarkPref, darkTheme, dynamicColor) }
        .collectLatest { (hasDarkPref, darkTheme, dynamicColor) ->
          val resolvedDark = if (hasDarkPref) darkTheme else isSystemDark()
          _state.update { it.copy(darkTheme = resolvedDark, dynamicColor = dynamicColor) }
        }
    }
  }

  private fun isSystemDark(): Boolean {
    val mode = application.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return mode == Configuration.UI_MODE_NIGHT_YES
  }

  fun onEvent(event: MainActivityEvent) {
    when (event) {
      MainActivityEvent.Load -> onLoad()
    }
  }
}
