package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastemanagement.data.local.database.toAccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.data.remote.toAccountFcmTokenRequest
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.screen.metadata.ImportMetadataExtension
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

  val importMetadataExtension = ImportMetadataExtension(this)

  init {
    viewModelScope.launch {
      _state.value = _state.value.copy(
        screenState = if (database.accountDao.query().isEmpty()) {
          ScreenState.Empty
        } else {
          ScreenState.Load
        },
      )
    }
  }

  fun onEvent(event: MainActivityEvent) {
    when (event) {
      is MainActivityEvent.Import -> importMetadataExtension.onImport(event)
      is MainActivityEvent.ChangeScreenState -> _state.update {
        it.copy(screenState = event.state)
      }
    }
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
