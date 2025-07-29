package net.techandgraphics.wastical.ui.screen.client.settings

import android.accounts.AccountManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.toAccountContactUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.getAccount
import javax.inject.Inject

@HiltViewModel
class ClientSettingsViewModel @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val preferences: Preferences,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientSettingsState>(ClientSettingsState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(ClientSettingsEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager) ?: return@launch
    val company = database.companyDao.query().first().toCompanyUiModel()
    val contacts = database.accountContactDao.query().map { it.toAccountContactUiModel() }
    val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
    val paymentPlan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
    preferences.flowOf<Boolean>(Preferences.DYNAMIC_COLOR, false)
      .collectLatest { dynamicColor ->
        _state.value = ClientSettingsState.Success(
          company = company,
          account = account,
          contacts = contacts,
          dynamicColor = dynamicColor,
          plan = paymentPlan,
        )
      }
  }

  private fun onButtonDynamicColor(event: ClientSettingsEvent.Button.DynamicColor) =
    viewModelScope.launch {
      if (_state.value is ClientSettingsState.Success) {
        preferences.put<Boolean>(Preferences.DYNAMIC_COLOR, event.isEnabled)
        _state.value = (_state.value as ClientSettingsState.Success)
          .copy(dynamicColor = event.isEnabled)
      }
    }

  fun onEvent(event: ClientSettingsEvent) {
    when (event) {
      ClientSettingsEvent.Load -> onLoad()
      is ClientSettingsEvent.Button.DynamicColor -> onButtonDynamicColor(event)
      else -> Unit
    }
  }
}
