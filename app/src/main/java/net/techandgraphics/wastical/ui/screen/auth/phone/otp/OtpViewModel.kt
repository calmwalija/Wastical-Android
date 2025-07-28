package net.techandgraphics.wastical.ui.screen.auth.phone.otp

import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.domain.toAccountUiModel
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

  private fun onOtp(event: OtpEvent.Otp) = viewModelScope.launch {
    if (event.opt.isDigitsOnly().not()) return@launch
    if (database.accountOtpDao.getByOpt(event.opt.toInt()).isEmpty()) {
      _channel.send(OtpChannel.Error(IllegalStateException("Invalid OTP")))
      return@launch
    }
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
      _channel.send(OtpChannel.Error(e))
    }
  }

  fun onEvent(event: OtpEvent) {
    when (event) {
      is OtpEvent.Load -> onLoad(event)
      is OtpEvent.Otp -> onOtp(event)
      else -> Unit
    }
  }
}
