package net.techandgraphics.quantcal.ui.activity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.data.local.database.toAccountFcmTokenEntity
import net.techandgraphics.quantcal.data.remote.account.ACCOUNT_ID
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.data.remote.toAccountFcmTokenRequest
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

  private suspend fun syncFcmToken() {
    database.accountFcmTokenDao.query()
      .filterNot { it.sync.not() }
      .map { it.toAccountFcmTokenRequest(ACCOUNT_ID) }
      .onEach {
        runCatching { api.fcmToken(it) }
          .onSuccess {
            database.withTransaction {
              with(database.accountFcmTokenDao) {
                deleteAll()
                insert(it.toAccountFcmTokenEntity())
              }
            }
          }
          .onFailure { println(mapApiError(it)) }
      }
  }
}
