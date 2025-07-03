package net.techandgraphics.quantcal.ui.screen.auth.phone.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.ui.screen.auth.phone.verify.VerifyPhoneChannel.Response
import net.techandgraphics.quantcal.ui.screen.auth.phone.verify.VerifyPhoneEvent.Input
import javax.inject.Inject

@HiltViewModel
class VerifyPhoneViewModel @Inject constructor(
  private val accountApi: AccountApi,
) : ViewModel() {

  private val _channel = Channel<VerifyPhoneChannel>()
  private val _state = MutableStateFlow(VerifyPhoneState())
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()
  private var job: Job? = null

  private fun onVerify() {
    job = viewModelScope.launch {
      job?.cancel()
      delay(2_000)
      val contact = state.value.contact.takeLast(9)
      _channel.send(Response.Success(contact))
      return@launch

      runCatching { accountApi.verify(contact) }
        .onSuccess { _channel.send(Response.Success(contact)) }
        .onFailure { _channel.send(Response.Failure(mapApiError(it))) }
    }
  }

  private fun onInputPhone(event: Input.Phone) {
    _state.update { it.copy(contact = event.value) }
  }

  fun onEvent(event: VerifyPhoneEvent) {
    when (event) {
      is Input.Phone -> onInputPhone(event)
      VerifyPhoneEvent.Button.Verify -> onVerify()
      else -> Unit
    }
  }
}
