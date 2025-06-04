package net.techandgraphics.wastemanagement.ui.screen.company.client.plan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyClientPlanViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyClientPlanState>(CompanyClientPlanState.Loading)

  val state = _state.asStateFlow()

  private fun onLoad(event: CompanyClientPlanEvent.Load) =
    viewModelScope.launch {
      _state.value = CompanyClientPlanState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val paymentPlans = database.paymentPlanDao.query()
        .mapIndexed { index, plan ->
          plan.toPaymentPlanUiModel().copy(belongTo = accountPlan.paymentPlanId == plan.id)
        }
      _state.value = CompanyClientPlanState.Success(
        account = account,
        paymentPlans = paymentPlans,
      )
    }

  fun onEvent(event: CompanyClientPlanEvent) {
    when (event) {
      is CompanyClientPlanEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
