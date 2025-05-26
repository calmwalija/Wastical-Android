package net.techandgraphics.wastemanagement.ui.activity.main.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastemanagement.domain.toAccountContactUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toAreaUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toDistrictUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import net.techandgraphics.wastemanagement.domain.toStreetUiModel
import net.techandgraphics.wastemanagement.domain.toTrashCollectionScheduleUiModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
  private val accountSession: AccountSessionRepository,
  private val database: AppDatabase,
  private val imageLoader: ImageLoader,
) : ViewModel() {

  private val _state = MutableStateFlow(MainActivityState())

  val state = _state.onStart {
    viewModelScope.launch {
      launch { getImageLoader() }
      launch { getAccount() }
      launch { getPayments() }
      launch { getInvoices() }
      launch { getAccountContact() }
      launch { getPaymentPlans() }
      launch { getCompanies() }
      launch { getCompanyContact() }
      launch { getPaymentMethods() }
      launch { getStreets() }
      launch { getAreas() }
      launch { getDistricts() }
      launch { getTrashCollectionSchedules() }
    }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000L),
    initialValue = MainActivityState(),
  )

  init {
    viewModelScope.launch {
      accountSession.clientSession()
    }
  }

  private fun getImageLoader() = _state.update { it.copy(imageLoader = imageLoader) }

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

  private suspend fun getTrashCollectionSchedules() {
    database.trashScheduleDao.flow()
      .map { dbTCSchedules -> dbTCSchedules.map { it.toTrashCollectionScheduleUiModel() } }
      .collectLatest { tCSchedules -> _state.update { it.copy(trashSchedules = tCSchedules) } }
  }

  private suspend fun getStreets() {
    database.streetDao.flow()
      .map { dbStreets -> dbStreets.map { it.toStreetUiModel() } }
      .collectLatest { streets -> _state.update { it.copy(streets = streets) } }
  }

  private suspend fun getAreas() {
    database.areaDao.flow()
      .map { dbAreas -> dbAreas.map { it.toAreaUiModel() } }
      .collectLatest { areas -> _state.update { it.copy(areas = areas) } }
  }

  private suspend fun getDistricts() {
    database.districtDao.flow()
      .map { dbDistricts -> dbDistricts.map { it.toDistrictUiModel() } }
      .collectLatest { districts -> _state.update { it.copy(districts = districts) } }
  }
}
