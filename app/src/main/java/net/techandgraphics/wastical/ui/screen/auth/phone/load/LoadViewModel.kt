package net.techandgraphics.wastical.ui.screen.auth.phone.load

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.worker.SESSION_WORKER_UUID
import net.techandgraphics.wastical.worker.scheduleAccountSessionWorker
import javax.inject.Inject

@HiltViewModel
class LoadViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _channel = Channel<LoadChannel>()
  val channel = _channel.receiveAsFlow()
  private val _state = MutableStateFlow<LoadState>(LoadState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad() = viewModelScope.launch {
    val companyInfo = database.companyDao.query()
    val account = authenticatorHelper.getAccount(accountManager)
    _state.value = LoadState.Success(account = account)
    if (account == null) {
      _channel.send(element = LoadChannel.NoAccount)
      return@launch
    }
    if (companyInfo.isEmpty()) {
      observeAccountSessionWorker()
      application.scheduleAccountSessionWorker()
    } else {
      launch { _channel.send(LoadChannel.Success) }
    }
  }

  private fun onLogout() = viewModelScope.launch {
    runCatching {
      authenticatorHelper.deleteAccounts()
      database.withTransaction { database.clearAllTables() }
    }
    _channel.send(element = LoadChannel.NoAccount)
  }

  private fun observeAccountSessionWorker() {
    viewModelScope.launch {
      WorkManager
        .getInstance(application)
        .getWorkInfosForUniqueWorkFlow(SESSION_WORKER_UUID)
        .mapNotNull { workInfoList -> workInfoList.firstOrNull() }
        .collect { workInfo ->
          when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> _channel.send(LoadChannel.Success)
            else -> Unit
          }
        }
    }
  }

  fun onEvent(event: LoadEvent) {
    when (event) {
      LoadEvent.Load -> onLoad()
      LoadEvent.Logout -> onLogout()
      else -> Unit
    }
  }
}
