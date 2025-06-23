package net.techandgraphics.wastemanagement.ui.screen.company.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentRequestUiModel
import net.techandgraphics.wastemanagement.getToday
import javax.inject.Inject

@HiltViewModel
class CompanyHomeViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyHomeState>(CompanyHomeState.Loading)
  val state = _state
    .onStart {
      viewModelScope.launch {
        database.accountDao.flow().collectLatest {
          if (it.isNotEmpty()) {
            onEvent(CompanyHomeEvent.Load)
          }
        }
      }
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = CompanyHomeState.Loading,
    )

  private fun onLoad() = viewModelScope.launch(Dispatchers.IO) {
    val (day, month, year) = getToday()
    val payment4CurrentLocationMonth =
      database.streetIndicatorDao.getPayment4CurrentLocationMonth(month, year)
    val payment4CurrentMonth = database.accountIndicatorDao.getPayment4CurrentMonth(month, year)
    val account = database.accountDao.get(ACCOUNT_ID).toAccountUiModel()
    val pending = database.paymentRequestDao.query().map { it.toPaymentRequestUiModel() }
    val company = database.companyDao.query().first().toCompanyUiModel()
    val companyContact = database.companyContactDao.query().first().toCompanyContactUiModel()
    val accountsSize = database.accountDao.getSize()
    val expectedAmountToCollect = database.paymentIndicatorDao.getExpectedAmountToCollect()
    val paymentPlanAgainstAccounts = database.paymentIndicatorDao.getPaymentPlanAgainstAccounts()
    _state.value = CompanyHomeState.Success(
      payment4CurrentMonth = payment4CurrentMonth,
      pending = pending,
      accountsSize = accountsSize,
      payment4CurrentLocationMonth = payment4CurrentLocationMonth,
      company = company,
      account = account,
      companyContact = companyContact,
      expectedAmountToCollect = expectedAmountToCollect,
      paymentPlanAgainstAccounts = paymentPlanAgainstAccounts,
    )
  }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      is CompanyHomeEvent.Load -> onLoad()
      else -> Unit
    }
  }
}
