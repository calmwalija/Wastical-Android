package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyClientProfileViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyClientProfileState>(CompanyClientProfileState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: CompanyClientProfileEvent.Load) =
    viewModelScope.launch {
      _state.value = CompanyClientProfileState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val paymentPlans = database.paymentPlanDao.query()
        .mapIndexed { index, plan ->
          plan.toPaymentPlanUiModel().copy(belongTo = accountPlan.paymentPlanId == plan.id)
        }
      launch { getPayments(account) }
      _state.value = CompanyClientProfileState.Success(
        account = account,
        paymentPlans = paymentPlans,
      )
    }

  private fun getState() = (_state.value as CompanyClientProfileState.Success)

  private fun getPayments(account: AccountUiModel) = viewModelScope.launch {
    database.paymentDao.flowOfByAccountId(account.id)
      .map { flowOf -> flowOf.map { it.toPaymentUiModel() } }
      .collectLatest { payments -> _state.value = getState().copy(payments = payments) }
  }

  fun onEvent(event: CompanyClientProfileEvent) {
    when (event) {
      is CompanyClientProfileEvent.Load -> onLoad(event)
      else -> TODO("Handle actions")
    }
  }
}
