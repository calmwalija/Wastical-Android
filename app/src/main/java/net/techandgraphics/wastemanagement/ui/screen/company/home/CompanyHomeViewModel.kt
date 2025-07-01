package net.techandgraphics.wastemanagement.ui.screen.company.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.Preferences
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastemanagement.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastemanagement.data.local.database.relations.toEntity
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.toAccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.toPaymentResponse
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentRequestUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastemanagement.getToday
import net.techandgraphics.wastemanagement.hash
import net.techandgraphics.wastemanagement.toZonedDateTime
import net.techandgraphics.wastemanagement.write
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CompanyHomeViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
  private val accountSession: AccountSessionRepository,
  private val preferences: Preferences,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyHomeState>(CompanyHomeState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyHomeChannel>()
  val channel = _channel.receiveAsFlow()

  init {
    viewModelScope.launch {
      database.accountDao.flow().collectLatest {
        if (it.isNotEmpty()) {
          onEvent(CompanyHomeEvent.Load)
        }
      }
    }
  }

  fun dateFormat(timestamp: Long, patten: String = "d-MMMM-yyyy"): String {
    val simpleDateFormat = SimpleDateFormat(patten, Locale.getDefault())
    val currentTimeMillis = Date(timestamp)
    return simpleDateFormat.format(currentTimeMillis)
  }

  private fun onExportMetadata() = viewModelScope.launch(Dispatchers.IO) {
    if (_state.value is CompanyHomeState.Success) {
      val state = (_state.value as CompanyHomeState.Success)
      val currentTimeMillis = System.currentTimeMillis()
      val fileName = "${state.company.name}-BackUp-${dateFormat(currentTimeMillis)}.json"
      val payments = database.paymentRequestDao.query()
        .map { it.toPaymentResponse() }
      val plans = database.accountPaymentPlanRequestDao.query()
        .map { it.toAccountPaymentPlanResponse() }
      val toExportData = CompanyMetaData(
        payments = payments,
        plans = plans,
      )
      val hashable = currentTimeMillis.hash(toExportData.toHash())
      val jsonToExport = Gson().toJson(toExportData.copy(hashable = hashable))
      val file = application.write(jsonToExport, fileName)
      _channel.send(CompanyHomeChannel.Export(file))
    }
  }

  private fun onLoad() = viewModelScope.launch(Dispatchers.IO) {
    val (_, month, year) = getToday()
    val default = Gson().toJson(MonthYear(month, year))
    combine(
      database.paymentDao.qPaymentWithAccountAndMethodWithGatewayLimit(limit = 3),
      preferences.flowOf<String>(Preferences.CURRENT_WORKING_MONTH, default),
    ) { timeline, jsonString ->
      val theTimeline = timeline.map {
        it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
      }
      val monthYear = Gson().fromJson(jsonString, MonthYear::class.java)
      val payment4CurrentLocationMonth =
        database.streetIndicatorDao.getPayment4CurrentLocationMonth(
          month = monthYear.month,
          year = monthYear.year,
        )

      // TODO - This is not working ❌
      val startEpoch =
        getToday().copy(year = monthYear.year, day = 20, month = monthYear.month.minus(1))
          .toZonedDateTime().toEpochSecond()

      val endEpoch =
        getToday().copy(year = monthYear.year, day = 20, month = monthYear.month.plus(1))
          .toZonedDateTime().toEpochSecond()

      val currentMonthCollected =
        database.paymentDao.getPaymentInRange(month, year, startEpoch, endEpoch)
      // TODO - End

      val account = database.accountDao.get(ACCOUNT_ID).toAccountUiModel()
      val pending = database.paymentRequestDao.query().map { it.toPaymentRequestUiModel() }
      val company = database.companyDao.query().first().toCompanyUiModel()
      val companyContact = database.companyContactDao.query().first().toCompanyContactUiModel()
      val accountsSize = database.accountDao.getSize()
      val expectedAmountToCollect = database.paymentIndicatorDao.getExpectedAmountToCollect()
      val paymentPlanAgainstAccounts =
        database.paymentIndicatorDao.getPaymentPlanAgainstAccounts()
      val allMonthsPayments = database.paymentIndicatorDao
        .getAllMonthsPayments()
        .map {
          MonthYearPayment4Month(
            monthYear = it,
            payment4CurrentMonth = database.accountIndicatorDao.getPayment4CurrentMonth(
              it.month,
              it.year,
            ),
          )
        }
      _state.value = CompanyHomeState.Success(
        payment4CurrentMonth = allMonthsPayments.first { it.monthYear == monthYear }.payment4CurrentMonth,
        pending = pending,
        accountsSize = accountsSize,
        payment4CurrentLocationMonth = payment4CurrentLocationMonth,
        company = company,
        account = account,
        companyContact = companyContact,
        expectedAmountToCollect = expectedAmountToCollect,
        paymentPlanAgainstAccounts = paymentPlanAgainstAccounts,
        allMonthsPayments = allMonthsPayments,
        monthYear = monthYear,
        timeline = theTimeline,
        currentMonthCollected = currentMonthCollected,
      )
    }.launchIn(this)
  }

  private fun onButtonWorkingMonth(event: CompanyHomeEvent.Button.WorkingMonth) =
    viewModelScope.launch {
      preferences.put<String>(Preferences.CURRENT_WORKING_MONTH, Gson().toJson(event.param))
    }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      is CompanyHomeEvent.Load -> onLoad()
      CompanyHomeEvent.Button.Export -> onExportMetadata()
      is CompanyHomeEvent.Button.WorkingMonth -> onButtonWorkingMonth(event)
      else -> Unit
    }
  }
}
