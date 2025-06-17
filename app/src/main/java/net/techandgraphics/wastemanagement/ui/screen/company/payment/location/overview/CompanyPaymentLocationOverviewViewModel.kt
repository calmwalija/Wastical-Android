package net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toAreaUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toStreetUiModel
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentLocationOverviewViewModel @Inject constructor(
  private val database: AppDatabase,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentLocationOverviewState>(CompanyPaymentLocationOverviewState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: CompanyPaymentLocationOverviewEvent.Load) =
    viewModelScope.launch {
      val calendar = Calendar.getInstance()
      val month = calendar.get(Calendar.MONTH).plus(1)
      val year = calendar.get(Calendar.YEAR)

      val companyLocation = database.companyLocationDao.getByStreetId(event.id)

      val demographicStreet = database.demographicStreetDao
        .get(companyLocation.demographicStreetId)
        .toStreetUiModel()

      val payment4CurrentMonth = database.paymentIndicatorDao
        .getPayment4CurrentMonthByStreetId(companyLocation.demographicStreetId, month, year)

      val demographicArea = database.demographicAreaDao
        .get(companyLocation.demographicAreaId)
        .toAreaUiModel()
      val accounts = database.accountDao
        .qByCompanyLocationId(companyLocation.id)
        .map { it.toAccountUiModel() }

      val company = database.companyDao.query().first().toCompanyUiModel()
      val expectedAmountToCollect =
        database.paymentIndicatorDao.getExpectedAmountToCollectByStreetId(companyLocation.demographicStreetId)

      _state.value = CompanyPaymentLocationOverviewState.Success(
        company = company,
        demographicStreet = demographicStreet,
        demographicArea = demographicArea,
        accounts = accounts,
        payment4CurrentMonth = payment4CurrentMonth,
        expectedAmountToCollect = expectedAmountToCollect,
      )
    }

  fun onEvent(event: CompanyPaymentLocationOverviewEvent) {
    when (event) {
      is CompanyPaymentLocationOverviewEvent.Load -> onLoad(event)
      else -> Unit
    }
  }
}
