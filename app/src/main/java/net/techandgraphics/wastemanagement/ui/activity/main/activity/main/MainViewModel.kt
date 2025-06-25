package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastemanagement.data.local.database.toAccountFcmTokenEntity
import net.techandgraphics.wastemanagement.data.remote.ServerResponse
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.data.remote.toAccountFcmTokenRequest
import net.techandgraphics.wastemanagement.domain.toAccountContactUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toAreaUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toDistrictUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import net.techandgraphics.wastemanagement.domain.toStreetUiModel
import net.techandgraphics.wastemanagement.domain.toTrashCollectionScheduleUiModel
import net.techandgraphics.wastemanagement.getFile
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val accountSession: AccountSessionRepository,
  private val database: AppDatabase,
  private val api: AccountApi,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(MainActivityState())
  val state = _state.asStateFlow()
  private val _channel = Channel<MainActivityChannel>()
  val channel = _channel.receiveAsFlow()

  init {
    viewModelScope.launch {
      _channel.send(
        if (database.accountDao.query().isEmpty()) {
          MainActivityChannel.Empty
        } else {
          MainActivityChannel.Load
        },
      )
    }
  }

  private fun onImport(event: MainActivityEvent.Import) = viewModelScope.launch {
    runCatching {
      val jsonString = application.getFile(event.uri).bufferedReader().use { it.readText() }
      Gson().fromJson(jsonString, ServerResponse::class.java)
    }.onSuccess { metadata ->

      if (metadata.accounts == null) {
        _channel.send(MainActivityChannel.Import.Data(MainActivityChannel.Import.Status.Invalid))
        return@launch
      }

      var current = 0
      runCatching {
        database.withTransaction {
          accountSession.purseData(metadata) { total, done ->
            current += done
            _channel.send(MainActivityChannel.Import.Progress(total, current))
          }
        }
      }
        .onSuccess { _channel.send(MainActivityChannel.Load) }
        .onFailure { _channel.send(MainActivityChannel.Import.Data(MainActivityChannel.Import.Status.Error)) }
    }
      .onFailure { _channel.send(MainActivityChannel.Import.Data(MainActivityChannel.Import.Status.Error)) }
  }

  fun onEvent(event: MainActivityEvent) {
    when (event) {
      is MainActivityEvent.Import -> onImport(event)
    }
  }

  private suspend fun getAccount() {
    database.accountDao.flow()
      .map { dbAccounts -> dbAccounts.map { it.toAccountUiModel() } }
      .collectLatest { accounts -> _state.update { it.copy(accounts = accounts) } }
  }

  private suspend fun getAccountContact() {
    database.accountContactDao.flow()
      .map { dbAccountContacts -> dbAccountContacts.map { it.toAccountContactUiModel() } }
      .collectLatest { accountContacts -> _state.update { it.copy(accountContacts = accountContacts) } }
  }

  private suspend fun getCompanies() {
    database.companyDao.flow()
      .map { dbCompanies -> dbCompanies.map { it.toCompanyUiModel() } }
      .collectLatest { companies -> _state.update { it.copy(companies = companies) } }
  }

  private suspend fun getCompanyContact() {
    database.companyContactDao.flow()
      .map { dbCompanyContacts -> dbCompanyContacts.map { it.toCompanyContactUiModel() } }
      .collectLatest { companyContacts -> _state.update { it.copy(companyContacts = companyContacts) } }
  }

  private suspend fun getPayments() {
    database.paymentDao.flowOfPayment()
      .map { dbPayments -> dbPayments.map { it.toPaymentUiModel() } }
      .collectLatest { payments -> _state.update { it.copy(payments = payments) } }
  }

  private suspend fun getInvoices() {
    database.paymentDao.flowOfInvoice()
      .map { dbInvoices -> dbInvoices.map { it.toPaymentUiModel() } }
      .collectLatest { invoices -> _state.update { it.copy(invoices = invoices) } }
  }

  private suspend fun getPaymentPlans() {
    database.paymentPlanDao.flow()
      .map { dbPaymentPlans -> dbPaymentPlans.map { it.toPaymentPlanUiModel() } }
      .collectLatest { paymentPlans -> _state.update { it.copy(paymentPlans = paymentPlans) } }
  }

  private suspend fun getPaymentMethods() {
    database.paymentMethodDao.flow()
      .map { dbPaymentMethods -> dbPaymentMethods.map { it.toPaymentMethodUiModel() } }
      .collectLatest { paymentMethods -> _state.update { it.copy(paymentMethods = paymentMethods) } }
  }

  private suspend fun getPaymentGateways() {
    database.paymentGatewayDao.flow()
      .map { dbPaymentGateway -> dbPaymentGateway.map { it.toPaymentGatewayUiModel() } }
      .collectLatest { paymentGateways -> _state.update { it.copy(paymentGateways = paymentGateways) } }
  }

  private suspend fun getTrashCollectionSchedules() {
    database.companyBinCollectionDao.flow()
      .map { dbTCSchedules -> dbTCSchedules.map { it.toTrashCollectionScheduleUiModel() } }
      .collectLatest { tCSchedules -> _state.update { it.copy(trashSchedules = tCSchedules) } }
  }

  private suspend fun getStreets() {
    database.demographicStreetDao.flow()
      .map { dbStreets -> dbStreets.map { it.toStreetUiModel() } }
      .collectLatest { streets -> _state.update { it.copy(streets = streets) } }
  }

  private suspend fun getAreas() {
    database.demographicAreaDao.flow()
      .map { dbAreas -> dbAreas.map { it.toAreaUiModel() } }
      .collectLatest { areas -> _state.update { it.copy(areas = areas) } }
  }

  private suspend fun getDistricts() {
    database.demographicDistrictDao.flow()
      .map { dbDistricts -> dbDistricts.map { it.toDistrictUiModel() } }
      .collectLatest { districts -> _state.update { it.copy(districts = districts) } }
  }

  private suspend fun syncFcmToken() {
    database.accountFcmTokenDao.query()
      .filterNot { it.sync.not() }
      .map { it.toAccountFcmTokenRequest(ACCOUNT_ID) }
      .onEach {
        runCatching { api.fcmToken(it) }
          .onSuccess {
            database.withTransaction {
              with(database.accountFcmTokenDao) {
                deleteAll()
                insert(it.toAccountFcmTokenEntity())
              }
            }
          }
          .onFailure { println(mapApiError(it)) }
      }
  }
}
