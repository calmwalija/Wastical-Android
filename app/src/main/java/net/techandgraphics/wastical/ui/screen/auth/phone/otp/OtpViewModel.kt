package net.techandgraphics.wastical.ui.screen.auth.phone.otp

import android.accounts.AccountManager
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
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.getAccount
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _state = MutableStateFlow<OtpState>(OtpState.Loading)
  val state = _state.asStateFlow()
  private val _channel = Channel<OtpChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: OtpEvent.Load) = viewModelScope.launch {
    authenticatorHelper.getAccount(accountManager)
      ?.let { newAccount ->
        _state.value = OtpState.Success(phone = event.phone, account = newAccount)
      }
      ?: onGotoVerify()
  }

  private fun onOtp(event: OtpEvent.Otp) = viewModelScope.launch {
    if (event.opt.isDigitsOnly().not()) return@launch
    if (database.accountOtpDao.getByOpt(event.opt.toInt()).isEmpty()) {
      _channel.send(OtpChannel.Error(IllegalStateException("Invalid One Time Password")))
      return@launch
    }
    runCatching {
      database.withTransaction {
        if (_state.value is OtpState.Success) {
          val newAccount = (_state.value as OtpState.Success).account
          authenticatorHelper.addAccountPlain(newAccount)
          database.accountOtpDao.deleteAll()
          database.accountDao.delete(newAccount.toAccountEntity())
        }
      }
    }.onSuccess { _channel.send(OtpChannel.Success) }
      .onFailure { _channel.send(OtpChannel.Error(it)) }
  }

  private fun onGotoVerify() = viewModelScope.launch {
    database.withTransaction {
      if (_state.value is OtpState.Success) {
        val newAccount = (_state.value as OtpState.Success).account
        database.accountOtpDao.deleteAll()
        database.accountDao.delete(newAccount.toAccountEntity())
        authenticatorHelper.deleteAccounts()
      }
    }
    _channel.send(OtpChannel.Verify)
  }

  fun onEvent(event: OtpEvent) {
    when (event) {
      is OtpEvent.Load -> onLoad(event)
      is OtpEvent.Otp -> onOtp(event)
      OtpEvent.NotMe -> onGotoVerify()
      else -> Unit
    }
  }
}
