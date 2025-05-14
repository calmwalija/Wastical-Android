package net.techandgraphics.wastemanagement.ui.screen.payment

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
class PaymentViewModel @Inject constructor() : ViewModel() {

  private val _state = MutableStateFlow(PaymentState())
  private val _channel = Channel<PaymentChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = PaymentState(),
    )

  fun onEvent(event: PaymentEvent) {
    when (event) {
      else -> TODO("Handle actions")
    }
  }
}
