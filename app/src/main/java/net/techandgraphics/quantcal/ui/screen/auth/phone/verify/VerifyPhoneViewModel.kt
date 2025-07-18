package net.techandgraphics.quantcal.ui.screen.auth.phone.verify

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toAccountOtpEntity
import net.techandgraphics.quantcal.data.remote.account.otp.AccountOtpApi
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.ui.screen.auth.phone.verify.VerifyPhoneChannel.Response
import net.techandgraphics.quantcal.ui.screen.auth.phone.verify.VerifyPhoneEvent.Input
import java.time.ZonedDateTime
import java.util.UUID
import javax.inject.Inject

data class Sms(
  val contact: String,
  val uuid: String = UUID.randomUUID().toString(),
  val timestamp: Long = ZonedDateTime.now().toEpochSecond(),
)

@HiltViewModel
class VerifyPhoneViewModel @Inject constructor(
  private val accountOtpApi: AccountOtpApi,
  private val database: AppDatabase,
) : ViewModel() {

  private val _channel = Channel<VerifyPhoneChannel>()
  private val _state = MutableStateFlow(VerifyPhoneState())
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()
  private var job: Job? = null

  private fun onVerify() {
    job = viewModelScope.launch {
      job?.cancel()
      val contact = state.value.contact.takeLast(9)
      val sms = Sms(contact = "993563408")
      runCatching { accountOtpApi.otp(sms.contact) }
        .onSuccess { otpResponse ->
          database.accountOtpDao.insert(otpResponse.toAccountOtpEntity())
          _channel.send(Response.Success(sms))
        }
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
