package net.techandgraphics.wastical.ui.screen.client.settings

import android.accounts.AccountManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
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
    _state.value = ClientSettingsState.Success(
      company = company,
      account = account,
      contacts = contacts,
      plan = paymentPlan,
    )
  }

  fun onEvent(event: ClientSettingsEvent) {
    when (event) {
      ClientSettingsEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
