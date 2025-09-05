package net.techandgraphics.wastical.ui.screen.auth.phone.otp

import android.accounts.AccountManager
import android.app.Application
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
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.Preferences.Companion.FCM_TOKEN_KEY
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.token.AccountFcmTokenEntity
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.ui.screen.AccountLogout
import net.techandgraphics.wastical.ui.screen.auth.phone.load.LoginState
import net.techandgraphics.wastical.worker.client.payment.scheduleClientPaymentDueReminderWorker
import net.techandgraphics.wastical.worker.scheduleAccountFcmTokenWorker
import net.techandgraphics.wastical.worker.scheduleAccountLastUpdatedPeriodicWorker
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val accountLogout: AccountLogout,
  private val preferences: Preferences,
  private val application: Application,
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
      _channel.send(OtpChannel.Error(IllegalStateException("Invalid Verification Code")))
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
    }.onSuccess {
      val newAccount = (_state.value as OtpState.Success).account
      if (newAccount.role == AccountRole.Client.name) {
        application.scheduleClientPaymentDueReminderWorker()
      }
      application.scheduleAccountLastUpdatedPeriodicWorker()
      onFcmToken()
      preferences.put(Preferences.LOGIN_STATE, LoginState.Login.name)
      _channel.send(OtpChannel.Success)
    }.onFailure { _channel.send(OtpChannel.Error(it)) }
  }

  private fun onFcmToken() = viewModelScope.launch {
    preferences.get(FCM_TOKEN_KEY, "")
      .takeIf { it.isNotEmpty() }
      ?.let { fcmToken ->
        database.accountFcmTokenDao.deleteAll()
        database.accountFcmTokenDao.upsert(AccountFcmTokenEntity(token = fcmToken))
        application.scheduleAccountFcmTokenWorker()
      }
  }

  private fun onGotoVerify() = viewModelScope.launch {
    accountLogout
      .invoke()
      .onSuccess { _channel.send(OtpChannel.Verify) }
      .onFailure { exception -> exception.printStackTrace() }
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
