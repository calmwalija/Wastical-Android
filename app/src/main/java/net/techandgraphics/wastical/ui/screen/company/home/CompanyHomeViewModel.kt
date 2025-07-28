package net.techandgraphics.wastical.ui.screen.company.home

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.data.remote.toAccountPaymentPlanResponse
import net.techandgraphics.wastical.data.remote.toPaymentResponse
import net.techandgraphics.wastical.domain.toCompanyContactUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentRequestUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.hash
import net.techandgraphics.wastical.write
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
  private val accountHelper: AuthenticatorHelper,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyHomeState>(CompanyHomeState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyHomeChannel>()
  val channel = _channel.receiveAsFlow()

  init {
    onEvent(CompanyHomeEvent.Load)
  }

  private fun onFetchChanges() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager) ?: return@launch
    _channel.send(CompanyHomeChannel.Fetch.Fetching)
    runCatching { accountSession.fetch(account.id) }
      .onSuccess { data ->
        try {
          database.withTransaction {
            database.clearAllTables()
            accountSession.purseData(data) { _, _ -> }
          }
          _channel.send(CompanyHomeChannel.Fetch.Success)
        } catch (e: Exception) {
          _channel.send(CompanyHomeChannel.Fetch.Error(mapApiError(e)))
        }
      }
      .onFailure { _channel.send(CompanyHomeChannel.Fetch.Error(mapApiError(it))) }
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
    authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        val (_, month, year) = getToday()
        val default = Gson().toJson(MonthYear(month, year))
        combine(
          database.paymentDao.qPaymentWithAccountAndMethodWithGatewayLimit(limit = 4),
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
          val upfrontPayments =
            database.paymentDao.qUpfrontPayments(monthYear.month, monthYear.year)
          val pending = database.paymentRequestDao.query().map { it.toPaymentRequestUiModel() }
          if (database.companyDao.query().isEmpty()) {
            _channel.send(CompanyHomeChannel.Goto.Reload)
            return@combine
          }
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
            }.sortedWith(
              compareByDescending<MonthYearPayment4Month> { it.monthYear.year }
                .thenByDescending { it.monthYear.month },
            )
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
            upfrontPayments = upfrontPayments,
          )
        }.launchIn(this)
      } ?: _channel.send(CompanyHomeChannel.Goto.Reload)
  }

  private fun onButtonWorkingMonth(event: CompanyHomeEvent.Button.WorkingMonth) =
    viewModelScope.launch {
      preferences.put<String>(Preferences.CURRENT_WORKING_MONTH, Gson().toJson(event.param))
    }

  private fun onLogout() = viewModelScope.launch {
    _channel.send(CompanyHomeChannel.Goto.Login)
    accountHelper.deleteAccounts()
  }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      CompanyHomeEvent.Fetch -> onFetchChanges()
      is CompanyHomeEvent.Load -> onLoad()
      CompanyHomeEvent.Button.Logout -> onLogout()
      is CompanyHomeEvent.Button.WorkingMonth -> onButtonWorkingMonth(event)

      else -> Unit
    }
  }
}
