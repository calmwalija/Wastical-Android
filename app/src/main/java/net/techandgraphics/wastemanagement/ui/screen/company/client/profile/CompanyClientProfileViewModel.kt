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
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentRequestUiModel
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
      val company = database.companyDao.query().first().toCompanyUiModel()
      val account = database.accountDao.get(event.id).toAccountUiModel()
      database.paymentRequestDao.qByAccountId(account.id)
        .map { entity -> entity.map { it.toPaymentRequestUiModel() } }
        .collectLatest { pending ->
          _state.value = CompanyClientProfileState.Success(
            company = company,
            account = account,
            pending = pending,
          )
          getPayments(account)
        }
    }

  private suspend fun getPayments(account: AccountUiModel) {
    database.paymentDao.flowOfByAccountId(account.id)
      .map { flowOf -> flowOf.map { it.toPaymentUiModel() } }
      .collectLatest { payments ->
        if (_state.value is CompanyClientProfileState.Success) {
          _state.value = (_state.value as CompanyClientProfileState.Success).copy(
            payments = payments,
          )
        }
      }
  }

  fun onEvent(event: CompanyClientProfileEvent) {
    when (event) {
      is CompanyClientProfileEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
