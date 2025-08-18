package net.techandgraphics.wastical.ui.screen.company.location.overview

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.Preferences
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.notification.request.NotificationRequestEntity
import net.techandgraphics.wastical.data.local.database.notification.template.NotificationTemplateScope
import net.techandgraphics.wastical.domain.toAccountRequestUiModel
import net.techandgraphics.wastical.domain.toAccountWithPaymentStatusUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toDemographicAreaUiModel
import net.techandgraphics.wastical.domain.toDemographicStreetUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.getReference
import net.techandgraphics.wastical.getToday
import net.techandgraphics.wastical.notification.NotificationType
import net.techandgraphics.wastical.worker.company.notification.scheduleCompanyNotificationRequestWorker
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentLocationOverviewViewModel @Inject constructor(
  private val database: AppDatabase,
  private val preferences: Preferences,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val application: Application,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentLocationOverviewState>(CompanyPaymentLocationOverviewState.Loading)
  val state = _state.asStateFlow()

  val templates: Flow<List<Pair<String, String>>> =
    database.notificationTemplateDao.flowOf(NotificationTemplateScope.LOCATION.name)
      .map { list -> list.map { it.title to it.body } }

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

          val company = database.companyDao.query().first().toCompanyUiModel()

          combine(
            flow = database.paymentIndicatorDao.flowOfAccountsWithPaymentStatusByStreetId(
              id = companyLocation.demographicStreetId,
              month = monthYear.month,
              year = monthYear.year,
            ).map { p0 -> p0.map { it.toAccountWithPaymentStatusUiModel() } },
            flow2 = database.accountRequestDao.flowOf()
              .map { p0 -> p0.map { it.toAccountRequestUiModel() } },
          ) { accounts, accountRequests ->
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
              accountRequests = accountRequests,
            )
          }.launchIn(viewModelScope)
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

  private fun onBroadcastSend(title: String, body: String) = viewModelScope.launch {
    if (_state.value is CompanyPaymentLocationOverviewState.Success) {
      val state = (_state.value as CompanyPaymentLocationOverviewState.Success)
      val localAccount = authenticatorHelper.getAccount(accountManager)!!
      val newNotification = NotificationRequestEntity(
        title = title,
        body = body,
        senderId = localAccount.id,
        topic = state.companyLocation.uuid,
        companyId = state.company.id,
        type = NotificationType.LOCATION_BASED_NOTIFICATION.name,
        recipientRole = AccountRole.Client.name,
        reference = getReference(),
      )
      database.notificationRequestDao.upsert(newNotification)
      application.scheduleCompanyNotificationRequestWorker()
    }
  }

  fun onEvent(event: CompanyPaymentLocationOverviewEvent) {
    when (event) {
      is CompanyPaymentLocationOverviewEvent.Load -> onLoad(event)
      is CompanyPaymentLocationOverviewEvent.Button.Broadcast -> Unit
      is CompanyPaymentLocationOverviewEvent.Broadcast.Send -> onBroadcastSend(event.title, event.body)
      is CompanyPaymentLocationOverviewEvent.Button.SortBy -> onSortBy(event)
      else -> Unit
    }
  }
}
