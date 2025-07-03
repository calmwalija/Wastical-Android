package net.techandgraphics.quantcal.ui.screen.company.client.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentRequestUiModel
import net.techandgraphics.quantcal.domain.toPaymentUiModel
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
      val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
        .toCompanyLocationWithDemographicUiModel()
      database.paymentRequestDao.qByAccountId(account.id)
        .map { entity -> entity.map { it.toPaymentRequestUiModel() } }
        .collectLatest { pending ->
          _state.value = CompanyClientProfileState.Success(
            company = company,
            account = account,
            pending = pending,
            demographic = demographic,
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
