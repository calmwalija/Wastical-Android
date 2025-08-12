package net.techandgraphics.wastical.ui.screen.company.client.info

import android.app.Application
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.AccountTitle
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.worker.company.account.scheduleCompanyAccountRequestWorker
import javax.inject.Inject

@HiltViewModel
class CompanyClientInfoViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyClientInfoState>(CompanyClientInfoState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyClientInfoChannel>()
  val channel = _channel.receiveAsFlow()
  private var contactAvailableJob: Job? = null

  private fun onLoad(event: CompanyClientInfoEvent.Load) = viewModelScope.launch {
    val company = database.companyDao.query().first().toCompanyUiModel()
    val account = database.accountDao.get(event.id).toAccountUiModel()
    val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
      .toCompanyLocationWithDemographicUiModel()
    _state.value = CompanyClientInfoState.Success(
      company = company,
      oldAccount = account,
      account = account.copy(username = account.username.takeIf { it.isDigitsOnly() } ?: ""),
      demographic = demographic,
    )
  }

  private fun checkIfContactAvailable(contact: String) {
    contactAvailableJob?.cancel()
    contactAvailableJob = viewModelScope.launch {
      delay(1_000)
      if (_state.value is CompanyClientInfoState.Success) {
        val state = (_state.value as CompanyClientInfoState.Success)
        val accounts = database.accountDao.qByUname(contact)
        if (accounts.map { it.accountId }.contains(state.account.id)
            .not() && accounts.isNotEmpty()
        ) {
          _channel.send(CompanyClientInfoChannel.Input.Unique.Conflict(accounts))
        } else {
          _channel.send(CompanyClientInfoChannel.Input.Unique.Ok)
        }
      }
    }
  }

  private fun onSubmit() = viewModelScope.launch {
    if (_state.value is CompanyClientInfoState.Success) {
      val state = (_state.value as CompanyClientInfoState.Success)
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(state.account.id)
      val plan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      val newUname = if (state.account.username.trim().isBlank()) {
        state.oldAccount.username
      } else {
        state.account.username.trim()
          .takeIf { it.isDigitsOnly() && it.length > 8 }
          ?.takeLast(9)
          ?: state.oldAccount.username
      }
      val newAccount = state.account.toAccountEntity().copy(username = newUname)

      runCatching {
        database.accountRequestDao.insert(
          newAccount
            .toAccountEntity(plan.id)
            .copy(httpOperation = HttpOperation.Put.name),
        )
      }.onSuccess {
        database.accountDao.update(newAccount)
        application.scheduleCompanyAccountRequestWorker()
        _channel.send(CompanyClientInfoChannel.Submit.Success)
      }.onFailure {
        _channel.send(
          CompanyClientInfoChannel.Submit
            .Error(mapApiError(it)),
        )
      }
    }
  }

  fun onEvent(event: CompanyClientInfoEvent) {
    when (event) {
      is CompanyClientInfoEvent.Load -> onLoad(event)
      is CompanyClientInfoEvent.Input.Type -> onInputType(event)
      CompanyClientInfoEvent.Button.Submit -> onSubmit()
      else -> Unit
    }
  }

  private fun onInputType(event: CompanyClientInfoEvent.Input.Type) {
    if (_state.value is CompanyClientInfoState.Success) {
      val state = (_state.value as CompanyClientInfoState.Success)
      when (event.ofType) {
        CompanyClientInfoEvent.Input.OfType.FName ->
          _state.value = state.copy(
            account = state.account.copy(
              firstname = event.newValue,
            ),
          )

        CompanyClientInfoEvent.Input.OfType.LName -> {
          _state.value = state.copy(
            account = state.account.copy(
              lastname = event.newValue,
            ),
          )
        }

        CompanyClientInfoEvent.Input.OfType.Contact -> {
          _state.value = state.copy(
            account = state.account.copy(
              username = event.newValue,
            ),
          )
          checkIfContactAvailable(event.newValue)
        }

        CompanyClientInfoEvent.Input.OfType.AltContact -> {
          Unit
        }

        CompanyClientInfoEvent.Input.OfType.Email ->
          _state.value = state.copy(
            account = state.account.copy(
              email = event.newValue,
            ),
          )

        CompanyClientInfoEvent.Input.OfType.Title ->
          _state.value = state.copy(
            account = state.account.copy(
              title = AccountTitle.valueOf(event.newValue),
            ),
          )
      }
    }
  }
}
