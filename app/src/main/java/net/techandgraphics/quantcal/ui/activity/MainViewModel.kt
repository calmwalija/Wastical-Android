package net.techandgraphics.quantcal.ui.activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  internal val accountSession: AccountSessionRepository,
  internal val database: AppDatabase,
  private val api: AccountApi,
  val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(MainActivityState())
  val state = _state.asStateFlow()
  internal val channelFlow = Channel<MainActivityChannel>()
  val channel = channelFlow.receiveAsFlow()

  init {
    viewModelScope.launch {
      if (database.accountDao.query().isEmpty()) {
        accountSession.fetchSession()
      }
    }
  }

  fun onEvent(event: MainActivityEvent) {
  }
}
