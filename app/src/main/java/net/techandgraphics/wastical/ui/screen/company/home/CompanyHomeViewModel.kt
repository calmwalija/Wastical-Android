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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYearPayment4Month
import net.techandgraphics.wastical.data.local.database.notification.request.NotificationRequestEntity
import net.techandgraphics.wastical.data.local.database.notification.template.NotificationTemplateScope
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.toAccountRequestUiModel
import net.techandgraphics.wastical.domain.toCompanyContactUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentRequestUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getReference
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.AccountLogout
import net.techandgraphics.wastical.worker.company.account.scheduleCompanyAccountRequestWorker
import net.techandgraphics.wastical.worker.company.notification.scheduleCompanyNotificationRequestWorker
import net.techandgraphics.wastical.worker.company.payment.scheduleCompanyPaymentRequestWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel class CompanyHomeViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
  private val accountSession: AccountSessionRepository,
  private val preferences: Preferences,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val accountLogout: AccountLogout,
) : ViewModel() {

  private val _state = MutableStateFlow<CompanyHomeState>(CompanyHomeState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<CompanyHomeChannel>()
  val channel = _channel.receiveAsFlow()

  val templates: Flow<List<Pair<String, String>>> =
    database.notificationTemplateDao.flowOf(NotificationTemplateScope.COMPANY.name)
      .map { list -> list.map { it.title to it.body } }

  init {
    onEvent(CompanyHomeEvent.Load)
  }

  private fun onFetchChanges() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager) ?: return@launch
    _channel.send(CompanyHomeChannel.Fetch.Fetching)
    runCatching { accountSession.fetch(account.id) }.onSuccess { data ->
      try {
        database.withTransaction {
          database.clearAllTables()
          accountSession.purseData(data) { _, _ -> }
        }
        _channel.send(CompanyHomeChannel.Fetch.Success)
      } catch (e: Exception) {
        _channel.send(CompanyHomeChannel.Fetch.Error(mapApiError(e)))
      }
    }.onFailure { _channel.send(CompanyHomeChannel.Fetch.Error(mapApiError(it))) }
  }

  fun dateFormat(timestamp: Long, patten: String = "d-MMMM-yyyy"): String {
    val simpleDateFormat = SimpleDateFormat(patten, Locale.getDefault())
    val currentTimeMillis = Date(timestamp)
    return simpleDateFormat.format(currentTimeMillis)
  }

  private fun onLoad() = viewModelScope.launch(Dispatchers.IO) {
    authenticatorHelper.getAccount(accountManager)?.let { account ->
      val (_, month, year) = getToday()
      val default = Gson().toJson(MonthYear(month, year))
      combine(
        database.paymentDao.qPaymentWithAccountAndMethodWithGatewayLimit(limit = 4),
        preferences.flowOf<String>(Preferences.CURRENT_WORKING_MONTH, default),
        database.paymentDao.qPaymentWithAccountAndMethodWithGateway(PaymentStatus.Verifying.name)
          .map { fromDb ->
            fromDb.map {
              it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
            }
          },
        database.accountRequestDao.flowOf().map { p0 -> p0.map { it.toAccountRequestUiModel() } },
        database.paymentRequestDao.flowOf()
          .map { p0 -> p0.map { it.toPaymentRequestUiModel() } },
      ) { timeline, jsonString, proofOfPayments, accountRequests, paymentRequests ->
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
        val allMonthsPayments = database.paymentIndicatorDao.getAllMonthsPayments().map {
          MonthYearPayment4Month(
            monthYear = it,
            payment4CurrentMonth = database.accountIndicatorDao.getPayment4CurrentMonth(
              it.month,
              it.year,
            ),
          )
        }.sortedWith(
          compareByDescending<MonthYearPayment4Month> { it.monthYear.year }.thenByDescending { it.monthYear.month },
        )
        val unpaidPerStreet = database.streetIndicatorDao.getUnpaidAccountsPerStreet()
        val accounts = database.accountDao.query()
        val newAccountsPerMonth = accounts
          .map { it.createdAt.toZonedDateTime().toLocalDate() }
          .groupBy { MonthYear(it.month.value, it.year) }
          .map { it.key to it.value.size }
          .sortedWith(compareBy<Pair<MonthYear, Int>>({ it.first.year }, { it.first.month }))
        _state.value = CompanyHomeState.Success(
          payment4CurrentMonth = allMonthsPayments.first { it.monthYear == monthYear }.payment4CurrentMonth,
          proofOfPayments = proofOfPayments,
          accountsSize = accountsSize,
          payment4CurrentLocationMonth = payment4CurrentLocationMonth,
          unpaidPerStreet = unpaidPerStreet,
          company = company,
          account = account,
          companyContact = companyContact,
          expectedAmountToCollect = expectedAmountToCollect,
          paymentPlanAgainstAccounts = paymentPlanAgainstAccounts,
          allMonthsPayments = allMonthsPayments,
          newAccountsPerMonth = newAccountsPerMonth,
          monthYear = monthYear,
          timeline = theTimeline,
          upfrontPayments = upfrontPayments,
          accountRequests = accountRequests,
          paymentRequests = paymentRequests,
        )
      }.launchIn(this)
    } ?: _channel.send(CompanyHomeChannel.Goto.Reload)
  }

  private fun onButtonWorkingMonth(event: CompanyHomeEvent.Button.WorkingMonth) =
    viewModelScope.launch {
      preferences.put<String>(Preferences.CURRENT_WORKING_MONTH, Gson().toJson(event.param))
    }

  private fun onLogout() = viewModelScope.launch {
    accountLogout
      .invoke()
      .onSuccess { _channel.send(CompanyHomeChannel.Goto.Login) }
  }

  private fun onButtonWorkers() = viewModelScope.launch {
    application.scheduleCompanyAccountRequestWorker()
    application.scheduleCompanyPaymentRequestWorker()
  }

  private fun onBroadcastSend(title: String, body: String) = viewModelScope.launch {
    if (_state.value is CompanyHomeState.Success) {
      val state = (_state.value as CompanyHomeState.Success)
      val localAccount = authenticatorHelper.getAccount(accountManager)!!
      val newNotification = NotificationRequestEntity(
        title = title,
        body = body,
        senderId = localAccount.id,
        topic = state.company.uuid,
        companyId = state.company.id,
        type = NotificationType.COMPANY_BROADCAST_NOTIFICATION.name,
        recipientRole = AccountRole.Client.name,
        reference = getReference(),
      )
      database.notificationRequestDao.upsert(newNotification)
      application.scheduleCompanyNotificationRequestWorker()
    }
  }

  fun onEvent(event: CompanyHomeEvent) {
    when (event) {
      CompanyHomeEvent.Fetch -> onFetchChanges()
      CompanyHomeEvent.Button.Workers -> onButtonWorkers()
      is CompanyHomeEvent.Load -> onLoad()
      CompanyHomeEvent.Button.Logout -> onLogout()
      is CompanyHomeEvent.Button.WorkingMonth -> onButtonWorkingMonth(event)
      CompanyHomeEvent.Button.Broadcast -> Unit
      is CompanyHomeEvent.Broadcast.Send -> onBroadcastSend(event.title, event.body)
      else -> Unit
    }
  }
}
