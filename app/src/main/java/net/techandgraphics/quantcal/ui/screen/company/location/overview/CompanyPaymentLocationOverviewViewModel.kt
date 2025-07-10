package net.techandgraphics.quantcal.ui.screen.company.location.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.Preferences
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.quantcal.domain.toAccountWithPaymentStatusUiModel
import net.techandgraphics.quantcal.domain.toCompanyLocationUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toDemographicAreaUiModel
import net.techandgraphics.quantcal.domain.toDemographicStreetUiModel
import net.techandgraphics.quantcal.getToday
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentLocationOverviewViewModel @Inject constructor(
  private val database: AppDatabase,
  private val preferences: Preferences,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentLocationOverviewState>(CompanyPaymentLocationOverviewState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: CompanyPaymentLocationOverviewEvent.Load) =
    viewModelScope.launch {
      val (_, cMonth, cYear) = getToday()
      val default = Gson().toJson(MonthYear(cMonth, cYear))
      preferences.flowOf<String>(Preferences.CURRENT_WORKING_MONTH, default)
        .collectLatest { jsonString ->
          val monthYear = Gson().fromJson(jsonString, MonthYear::class.java)

          val companyLocation = database.companyLocationDao.getByStreetId(event.id)
            .toCompanyLocationUiModel()

          val demographicStreet = database.demographicStreetDao
            .get(companyLocation.demographicStreetId)
            .toDemographicStreetUiModel()

          val payment4CurrentMonth = database.paymentIndicatorDao
            .getPayment4CurrentMonthByStreetId(
              companyLocation.demographicStreetId,
              monthYear.month,
              monthYear.year,
            )

          val demographicArea = database.demographicAreaDao
            .get(companyLocation.demographicAreaId)
            .toDemographicAreaUiModel()
          val accounts = database.paymentIndicatorDao.getAccountsWithPaymentStatusByStreetId(
            id = companyLocation.demographicStreetId,
            month = monthYear.month,
            year = monthYear.year,
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
            monthYear = monthYear,
          )
        }
    }

  private fun onSortBy(event: CompanyPaymentLocationOverviewEvent.Button.SortBy) =
    viewModelScope.launch {
      if (_state.value is CompanyPaymentLocationOverviewState.Success) {
        val state = (_state.value as CompanyPaymentLocationOverviewState.Success)
        val accounts = database.paymentIndicatorDao.getAccountsWithPaymentStatusByStreetId(
          id = state.companyLocation.demographicStreetId,
          month = state.monthYear.month,
          year = state.monthYear.year,
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
