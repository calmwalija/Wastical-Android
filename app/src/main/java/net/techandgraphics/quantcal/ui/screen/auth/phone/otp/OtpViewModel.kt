package net.techandgraphics.quantcal.ui.screen.auth.phone.otp

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.domain.toAccountUiModel
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountSessionRepository: AccountSessionRepository,
) : ViewModel() {

  private val _state = MutableStateFlow<OtpState>(OtpState.Loading)
  val state = _state.asStateFlow()
  private val _channel = Channel<OtpChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: OtpEvent.Load) {
    _state.value = OtpState.Success(phone = event.phone)
  }

  private var timerJob: Job? = null
  private val totalTimeMillis = 5 * 60 * 1000L

  fun startOrResumeTimer() {
    Log.e("TAG", "startOrResumeTimer: ")
    if (_state.value is OtpState.Success) {
      val state = (_state.value as OtpState.Success)
      if (state.isRunning) return
      _state.value = state.copy(isRunning = true)

      timerJob = viewModelScope.launch {
        while ((_state.value as OtpState.Success).timeLeft > 0) {
          delay(1000L)
          val timeLeft = (_state.value as OtpState.Success).timeLeft
          _state.value = (_state.value as OtpState.Success).copy(timeLeft = timeLeft - 1000L)
        }
        _state.value = state.copy(isRunning = false)
      }
    }
  }

  fun pauseTimer() {
    if (_state.value is OtpState.Success) {
      val state = (_state.value as OtpState.Success)
      _state.value = state.copy(isRunning = false)
      timerJob?.cancel()
    }
  }

  fun resetTimer() {
    pauseTimer()
    if (_state.value is OtpState.Success) {
      val state = (_state.value as OtpState.Success)
      _state.value = state.copy(timeLeft = totalTimeMillis)
    }
  }

  private fun onOtp(event: OtpEvent.Otp) = viewModelScope.launch {
    if (event.opt.isDigitsOnly().not()) return@launch
    if (database.accountOtpDao.getByOpt(event.opt.toInt()).isEmpty()) return@launch
    val otp = database.accountOtpDao.query().first()
    try {
      database.withTransaction {
        accountSessionRepository.purseData(
          accountSessionRepository.fetch(otp.accountId),
        ) { _, _ -> }
        val newAccount = database.accountDao.get(otp.accountId).toAccountUiModel()
        authenticatorHelper.addAccountPlain(newAccount)
        database.accountOtpDao.deleteAll()
        _channel.send(OtpChannel.Success)
      }
    } catch (e: Exception) {
      _channel.send(OtpChannel.Error(mapApiError(e)))
    }
  }

  fun onEvent(event: OtpEvent) {
    when (event) {
      is OtpEvent.Load -> onLoad(event)
      is OtpEvent.Otp -> onOtp(event)
      is OtpEvent.Timer -> when (event) {
        OtpEvent.Timer.Failed -> Unit
        OtpEvent.Timer.Pause -> pauseTimer()
        OtpEvent.Timer.Reset -> resetTimer()
        OtpEvent.Timer.Start -> startOrResumeTimer()
        OtpEvent.Timer.TimedOut -> resetTimer()
      }

      else -> Unit
    }
  }
}
