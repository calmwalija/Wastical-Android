package net.techandgraphics.wastemanagement.ui.screen.company.payment.location

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentPerLocationViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentPerLocationState>(CompanyPaymentPerLocationState.Loading)
  val state = _state.asStateFlow()

  init {
    onEvent(CompanyPaymentPerLocationEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    val calendar = Calendar.getInstance()
    val month = calendar.get(Calendar.MONTH).plus(1)
    val year = calendar.get(Calendar.YEAR)
    val payment4CurrentLocationMonth =
      database.paymentDao.qPayment4CurrentLocationMonth(month, year)
    val company = database.companyDao.query().first().toCompanyUiModel()
    _state.value = CompanyPaymentPerLocationState.Success(
      payment4CurrentLocationMonth = payment4CurrentLocationMonth,
      company = company,
    )
  }

  fun onEvent(event: CompanyPaymentPerLocationEvent) {
    when (event) {
      CompanyPaymentPerLocationEvent.Load -> onLoad()
      else -> TODO("Handle actions")
    }
  }
}
