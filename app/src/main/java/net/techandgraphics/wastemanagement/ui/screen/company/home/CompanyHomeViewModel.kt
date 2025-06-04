package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toPaymentAccountUiModel
import javax.inject.Inject

@HiltViewModel
class CompanyHomeViewModel @Inject constructor(
  private val database: AppDatabase,

) : ViewModel() {

  private val _state = MutableStateFlow(CompanyHomeState())
  val state = _state.asStateFlow()

  private suspend fun getPaymentAccounts() {
    database.paymentDao.flowOfPaymentAccount()
      .map { dbAccounts -> dbAccounts.map { it.toPaymentAccountUiModel() } }
      .collectLatest { payments ->
        _state.update { it.copy(payments = payments.take(5)) }
      }
  }

  private fun onAppState(event: CompanyHomeEvent.AppState) = viewModelScope.launch {
    _state.update { it.copy(state = event.state) }
    launch { getPaymentAccounts() }
  }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      is CompanyHomeEvent.AppState -> onAppState(event)
      else -> Unit
    }
  }
}
