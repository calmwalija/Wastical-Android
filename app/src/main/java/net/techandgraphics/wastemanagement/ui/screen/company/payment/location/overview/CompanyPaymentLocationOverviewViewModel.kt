package net.techandgraphics.wastemanagement.ui.screen.company.payment.location.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toAccountWithPaymentStatusUiModel
import net.techandgraphics.wastemanagement.domain.toAreaUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyLocationUiModel
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

  private val calendar = Calendar.getInstance()
  private val month = calendar.get(Calendar.MONTH).plus(1)
  private val year = calendar.get(Calendar.YEAR)

  private fun onLoad(event: CompanyPaymentLocationOverviewEvent.Load) =
    viewModelScope.launch {
      val companyLocation = database.companyLocationDao.getByStreetId(event.id)
        .toCompanyLocationUiModel()

      val demographicStreet = database.demographicStreetDao
        .get(companyLocation.demographicStreetId)
        .toStreetUiModel()

      val payment4CurrentMonth = database.paymentIndicatorDao
        .getPayment4CurrentMonthByStreetId(companyLocation.demographicStreetId, month, year)

      val demographicArea = database.demographicAreaDao
        .get(companyLocation.demographicAreaId)
        .toAreaUiModel()
      val accounts = database.paymentIndicatorDao.getAccountsWithPaymentStatusByStreetId(
        id = companyLocation.demographicStreetId,
        month = month,
        year = year,
      ).map { it.toAccountWithPaymentStatusUiModel() }

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
        companyLocation = companyLocation,
      )
    }

  private fun onSortBy(event: CompanyPaymentLocationOverviewEvent.Button.SortBy) =
    viewModelScope.launch {
      if (_state.value is CompanyPaymentLocationOverviewState.Success) {
        val state = (_state.value as CompanyPaymentLocationOverviewState.Success)
        val accounts = database.paymentIndicatorDao.getAccountsWithPaymentStatusByStreetId(
          id = state.companyLocation.demographicStreetId,
          month = month,
          year = year,
          sortOrder = event.sort.ordinal,
        ).map { it.toAccountWithPaymentStatusUiModel() }
        _state.value = (_state.value as CompanyPaymentLocationOverviewState.Success).copy(
          accounts = accounts,
          sortBy = event.sort,
        )
      }
    }

  fun onEvent(event: CompanyPaymentLocationOverviewEvent) {
    when (event) {
      is CompanyPaymentLocationOverviewEvent.Load -> onLoad(event)
      is CompanyPaymentLocationOverviewEvent.Button.SortBy -> onSortBy(event)
      else -> Unit
    }
  }
}
