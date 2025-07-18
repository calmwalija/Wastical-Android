package net.techandgraphics.qgateway.ui.activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.qgateway.data.local.database.QgatewayDatabase
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
  internal val database: QgatewayDatabase,
  val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<MainActivityState>(MainActivityState.Loading)
  val state = _state.asStateFlow()
  internal val channelFlow = Channel<MainActivityChannel>()
  val channel = channelFlow.receiveAsFlow()

  private fun onLoad() = viewModelScope.launch {
    database.smsDao.query()
      .map { p0 -> p0.map { it.toSmsUiModel() } }
      .collectLatest { messages ->
        _state.value = MainActivityState.Success(messages = messages)
      }
  }

  init {
    onEvent(MainActivityEvent.Load)
  }

  fun onEvent(event: MainActivityEvent) {
    when (event) {
      MainActivityEvent.Load -> onLoad()
    }
  }
}
