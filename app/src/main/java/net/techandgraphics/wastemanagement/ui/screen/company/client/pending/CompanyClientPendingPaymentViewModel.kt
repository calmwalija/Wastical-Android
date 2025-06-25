package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentRequestWithAccountUiModel
import javax.inject.Inject

@HiltViewModel class CompanyClientPendingPaymentViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyClientPendingPaymentState>(CompanyClientPendingPaymentState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: CompanyClientPendingPaymentEvent.Load) =
    viewModelScope.launch {
      database.paymentRequestDao.getWithAccountByAccountId(event.id)
        .map { entity -> entity.map { it.toPaymentRequestWithAccountUiModel() } }
        .collectLatest { pending ->
          val company = database.companyDao.query().first().toCompanyUiModel()
          val account = database.accountDao.get(event.id).toAccountUiModel()
          val demographic =
            database.companyLocationDao.getWithDemographic(account.companyLocationId)
              .toCompanyLocationWithDemographicUiModel()
          _state.value = CompanyClientPendingPaymentState.Success(
            company = company,
            pending = pending,
            account = account,
            demographic = demographic,
          )
        }
    }

  private fun onButtonDelete(event: CompanyClientPendingPaymentEvent.Button.Delete) =
    viewModelScope.launch {
      database.paymentRequestDao.delete(event.payment.toPaymentRequestEntity())
    }

  fun onEvent(event: CompanyClientPendingPaymentEvent) {
    when (event) {
      is CompanyClientPendingPaymentEvent.Load -> onLoad(event)
      is CompanyClientPendingPaymentEvent.Button.Delete -> onButtonDelete(event)
      else -> Unit
    }
  }
}
