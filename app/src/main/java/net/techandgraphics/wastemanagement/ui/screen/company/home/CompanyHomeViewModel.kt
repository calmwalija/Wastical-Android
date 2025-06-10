package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.getMonthStartTimestamp
import net.techandgraphics.wastemanagement.domain.toPaymentAccountUiModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CompanyHomeViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyHomeState>(CompanyHomeState.Loading)
  val state = _state.asStateFlow()

  private suspend fun getPaymentAccounts() {
    database.paymentDao.flowOfPaymentAccount()
      .map { dbAccounts -> dbAccounts.map { it.toPaymentAccountUiModel() } }
      .collectLatest { payments ->
        (_state.value as CompanyHomeState.Success).copy(payments = payments.take(5))
      }
  }

  private fun onLoad(event: CompanyHomeEvent.Load) = viewModelScope.launch {
    val calendar = Calendar.getInstance()
    val duration =
      getMonthStartTimestamp(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH).plus(1))
    val streetPaidThisMonth = database.streetIndicatorDao.getStreetPaidThisMonth(duration)
    val paidThisMonth = database.accountIndicatorDao.getPaidThisMonthIndicator(duration)
    val dailyPayments = database.paymentIndicatorDao.getDailyPaymentSummary()
    _state.value = CompanyHomeState.Success(
      state = event.state,
      paidThisMonth = paidThisMonth,
      streetPaidThisMonth = streetPaidThisMonth,
      dailyPayments = dailyPayments,
    )
    launch { getPaymentAccounts() }
  }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      is CompanyHomeEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
