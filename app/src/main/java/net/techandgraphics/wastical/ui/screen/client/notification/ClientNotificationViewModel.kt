package net.techandgraphics.wastical.ui.screen.client.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toNotificationUiModel
import javax.inject.Inject

@HiltViewModel
class ClientNotificationViewModel @Inject constructor(
  private val database: AppDatabase,
  private val gson: Gson,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientNotificationState>(ClientNotificationState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(ClientNotificationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    database.notificationDao.flowOf()
      .map { p0 -> p0.map { it.toNotificationUiModel() } }
      .collectLatest { notifications ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        _state.value = ClientNotificationState.Success(
          company = company,
          notifications = notifications,
        )
      }
  }

  fun onEvent(event: ClientNotificationEvent) {
    when (event) {
      ClientNotificationEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
