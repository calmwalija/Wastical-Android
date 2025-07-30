package net.techandgraphics.wastical.ui.screen.client.info

import android.accounts.AccountManager
import android.app.Application
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
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.worker.company.account.scheduleCompanyAccountRequestWorker
import javax.inject.Inject

@HiltViewModel
class ClientInfoViewModel @Inject constructor(
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientInfoState>(ClientInfoState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientInfoChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: ClientInfoEvent.Load) = viewModelScope.launch {
    val account = database.accountDao.get(event.id).toAccountUiModel()
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = ClientInfoState.Success(
      company = company,
      account = account,
      newAccount = account,
    )
  }

  private fun onButtonSubmit() = viewModelScope.launch {
    if (_state.value is ClientInfoState.Success) {
      val state = (_state.value as ClientInfoState.Success)
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(state.account.id)
      val plan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      val newAccount = state.newAccount.toAccountEntity()
      runCatching {
        database.accountRequestDao.insert(
          newAccount
            .toAccountEntity(plan.id)
            .copy(httpOperation = HttpOperation.Put.name),
        )
      }.onSuccess {
        database.accountDao.update(newAccount)
        application.scheduleCompanyAccountRequestWorker()
        authenticatorHelper.deleteAccounts()
        authenticatorHelper.addAccountPlain(newAccount.toAccountUiModel())
        _channel.send(ClientInfoChannel.Submit.Success)
      }.onFailure {
        _channel.send(ClientInfoChannel.Submit.Error(mapApiError(it)))
      }
    }
  }

  fun onEvent(event: ClientInfoEvent) {
    when (event) {
      is ClientInfoEvent.Load -> onLoad(event)
      ClientInfoEvent.Button.Submit -> onButtonSubmit()
      is ClientInfoEvent.Input.Type -> onInputType(event)
      else -> Unit
    }
  }

  private fun onInputType(event: ClientInfoEvent.Input.Type) {
    if (_state.value is ClientInfoState.Success) {
      val state = (_state.value as ClientInfoState.Success)
      when (event.ofType) {
        ClientInfoEvent.Input.OfType.FName ->
          _state.value = state.copy(
            newAccount = state.account.copy(
              firstname = event.newValue,
            ),
          )

        ClientInfoEvent.Input.OfType.LName -> {
          _state.value = state.copy(
            newAccount = state.account.copy(
              lastname = event.newValue,
            ),
          )
        }

        ClientInfoEvent.Input.OfType.Title ->
          _state.value = state.copy(
            newAccount = state.account.copy(
              title = AccountTitle.valueOf(event.newValue),
            ),
          )
      }
    }
  }
}
