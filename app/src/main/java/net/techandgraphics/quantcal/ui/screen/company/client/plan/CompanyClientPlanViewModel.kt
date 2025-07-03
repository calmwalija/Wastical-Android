package net.techandgraphics.quantcal.ui.screen.company.client.plan

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
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanRequestEntity
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.data.remote.toAccountPaymentPlanRequest
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyClientPlanViewModel @Inject constructor(
  private val database: AppDatabase,
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

      val oldAccountPlan = database.accountPaymentPlanDao.getByAccountId(state.account.id)
      val newAccountPlan = oldAccountPlan.copy(paymentPlanId = state.plan.id)
      val newAccountPlanRequest = newAccountPlan
        .toAccountPaymentPlanRequest()
        .toAccountPaymentPlanRequestEntity()

      runCatching {
        database.withTransaction {
          database.accountPaymentPlanDao.update(newAccountPlan)
          database.accountPaymentPlanRequestDao.deleteByAccountId(newAccountPlan.accountId)
          database.accountPaymentPlanRequestDao.insert(newAccountPlanRequest)
        }
      }
        .onFailure { _channel.send(CompanyClientPlanChannel.Error(mapApiError(it))) }
        .onSuccess { _channel.send(CompanyClientPlanChannel.Success) }
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
