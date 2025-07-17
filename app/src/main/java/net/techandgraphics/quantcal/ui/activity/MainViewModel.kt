package net.techandgraphics.quantcal.ui.activity

import android.accounts.AccountManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.getAccount
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  internal val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _state = MutableStateFlow(MainActivityState())
  val state = _state.asStateFlow()
  internal val channelFlow = Channel<MainActivityChannel>()
  val channel = channelFlow.receiveAsFlow()

  private fun onLoad() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager)
    _state.update { it.copy(account = account, isLoading = false, holding = false) }
  }

  private fun onNullify(event: MainActivityEvent.Nullify) = viewModelScope.launch {
    _state.update { it.copy(account = null, holding = true) }
    if (event.logout) {
      delay(3_000)
      runCatching { database.withTransaction { database.clearAllTables() } }
    }
  }

  init {
    onEvent(MainActivityEvent.Load)
  }

  fun onEvent(event: MainActivityEvent) {
    when (event) {
      MainActivityEvent.Load -> onLoad()
      is MainActivityEvent.Nullify -> onNullify(event)
    }
  }
}
