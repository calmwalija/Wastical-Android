package net.techandgraphics.wastical.ui.screen.client.info

import android.accounts.AccountManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.getAccount
import javax.inject.Inject

@HiltViewModel
class ClientInfoViewModel @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientInfoState>(ClientInfoState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientInfoChannel>()
  val channel = _channel.receiveAsFlow()

  init {
    onEvent(ClientInfoEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager) ?: return@launch
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = ClientInfoState.Success(
      company = company,
      account = account,
    )
  }

  fun onEvent(event: ClientInfoEvent) {
    when (event) {
      ClientInfoEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
