package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.data.remote.toAccountPaymentPlanRequest
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyClientPlanViewModel @Inject constructor(
  private val database: AppDatabase,
  private val accountApi: AccountApi,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyClientPlanState>(CompanyClientPlanState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyClientPlanChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: CompanyClientPlanEvent.Load) =
    viewModelScope.launch {
      _state.value = CompanyClientPlanState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val company = database.companyDao.query().first().toCompanyUiModel()
      val paymentPlans = database.paymentPlanDao.query()
        .mapIndexed { index, plan ->
          plan.toPaymentPlanUiModel().copy(active = accountPlan.paymentPlanId == plan.id)
        }
      val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
        .toCompanyLocationWithDemographicUiModel()

      _state.value = CompanyClientPlanState.Success(
        company = company,
        account = account,
        paymentPlans = paymentPlans,
        demographic = demographic,
        plan = paymentPlans.first { it.id == accountPlan.paymentPlanId },
      )
    }

  private fun onChange(event: CompanyClientPlanEvent.Button.ChangePlan) =
    viewModelScope.launch {
      val paymentPlans = database.paymentPlanDao.query()
        .mapIndexed { index, plan ->
          plan.toPaymentPlanUiModel().copy(active = event.plan.id == plan.id)
        }
      _state.value =
        (_state.value as CompanyClientPlanState.Success).copy(
          plan = event.plan,
          paymentPlans = paymentPlans,
        )
    }

  private fun onSubmit() = viewModelScope.launch {
    if (_state.value is CompanyClientPlanState.Success) {
      _channel.send(CompanyClientPlanChannel.Processing)
      val state = (_state.value as CompanyClientPlanState.Success)
      val request = state.plan.toAccountPaymentPlanRequest(state.account)
      runCatching { accountApi.plan(state.plan.id, request) }
        .onFailure { _channel.send(CompanyClientPlanChannel.Error(mapApiError(it))) }
        .onSuccess {
          database.accountPaymentPlanDao.update(it.toAccountPaymentPlanEntity())
          _channel.send(CompanyClientPlanChannel.Success)
        }
    }
  }

  fun onEvent(event: CompanyClientPlanEvent) {
    when (event) {
      is CompanyClientPlanEvent.Load -> onLoad(event)
      CompanyClientPlanEvent.Button.Submit -> onSubmit()
      is CompanyClientPlanEvent.Button.ChangePlan -> onChange(event)
      else -> Unit
    }
  }
}
