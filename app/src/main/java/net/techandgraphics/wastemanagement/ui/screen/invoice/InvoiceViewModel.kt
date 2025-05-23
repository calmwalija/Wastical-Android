package net.techandgraphics.wastemanagement.ui.screen.invoice

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
class InvoiceViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(InvoiceState())
  private val _channel = Channel<InvoiceChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = InvoiceState(),
    )

  fun onEvent(event: InvoiceEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
