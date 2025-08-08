package net.techandgraphics.wastical.ui.screen.company.client.profile

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.domain.toPaymentUiModel
import net.techandgraphics.wastical.worker.company.account.scheduleCompanyAccountRequestWorker
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyClientProfileViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyClientProfileState>(CompanyClientProfileState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyClientProfileChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: CompanyClientProfileEvent.Load) =
    viewModelScope.launch {
      _state.value = CompanyClientProfileState.Loading
      val company = database.companyDao.query().first().toCompanyUiModel()
      val account = database.accountDao.get(event.id).toAccountUiModel()

      if (database.accountRequestDao
          .query()
          .filter { it.httpOperation == HttpOperation.Post.name }
          .map { it.id }
          .contains(account.id)
      ) {
        _channel.send(CompanyClientProfileChannel.NewAccount)
      }

      val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
        .toCompanyLocationWithDemographicUiModel()
      combine(
        flow = database.paymentRequestDao.qWithAccountByAccountId(account.id)
          .map { entity -> entity.map { it.toPaymentRequestWithAccountUiModel() } },
        flow2 = database.paymentDao.flowOfByAccountId(account.id)
          .map { flowOf -> flowOf.map { it.toPaymentUiModel() } },
      ) { pending, payments ->
        _state.value = CompanyClientProfileState.Success(
          company = company,
          account = account,
          pending = pending,
          payments = payments,
          demographic = demographic,
        )
      }.launchIn(viewModelScope)
    }

  private fun onOptionRevoke() = viewModelScope.launch {
    if (_state.value is CompanyClientProfileState.Success) {
      val state = (_state.value as CompanyClientProfileState.Success)
      val timestamp = ZonedDateTime.now().toEpochSecond()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(state.account.id)
      val plan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      val newAccount = state.account.toAccountEntity()
        .copy(
          leavingReason = "Opt-out",
          leavingTimestamp = timestamp,
          updatedAt = timestamp,
          status = Status.Inactive.name,
        )
      runCatching {
        database.accountRequestDao.insert(
          newAccount
            .toAccountEntity(plan.id)
            .copy(httpOperation = HttpOperation.Put.name),
        )
      }.onSuccess {
        database.accountDao.update(newAccount)
        application.scheduleCompanyAccountRequestWorker()
        _channel.send(CompanyClientProfileChannel.Revoke.Success)
      }.onFailure {
        _channel.send(
          CompanyClientProfileChannel.Revoke
            .Error(mapApiError(it)),
        )
      }
    }
  }

  fun onEvent(event: CompanyClientProfileEvent) {
    when (event) {
      is CompanyClientProfileEvent.Load -> onLoad(event)
      is CompanyClientProfileEvent.Option.Revoke -> onOptionRevoke()
      else -> Unit
    }
  }
}
