package net.techandgraphics.wastemanagement.ui.screen.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.toAccountContactUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
import net.techandgraphics.wastemanagement.ui.screen.transaction.pdf.invoiceToPdf
import javax.inject.Inject

@HiltViewModel class HomeViewModel @Inject constructor(
  private val database: AppDatabase,
  private val imageLoader: ImageLoader,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow(HomeState())
  private val _channel = Channel<HomeChannel>()
  val channel = _channel.receiveAsFlow()

  val state = _state.onStart {
    _state.update { it.copy(imageLoader = imageLoader) }
    getAccount()
    getPayments()
    getPaymentPlans()
    getAccountContact()
    getCompany()
    getCompanyContact()
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5_000L),
    initialValue = HomeState(),
  )

  private suspend fun getAccount() {
    val accounts = database.accountDao.query().map { it.toAccountUiModel() }
    _state.update { it.copy(accounts = accounts) }
  }

  private suspend fun getAccountContact() {
    val accountContacts = database.accountContactDao.query().map { it.toAccountContactUiModel() }
    _state.update { it.copy(accountContacts = accountContacts) }
  }

  private suspend fun getCompany() {
    val company = database.companyDao.query().map { it.toCompanyUiModel() }
    _state.update { it.copy(company = company) }
  }

  private suspend fun getCompanyContact() {
    val companyContacts = database.companyContactDao.query().map { it.toCompanyContactUiModel() }
    _state.update { it.copy(companyContacts = companyContacts) }
  }

  private suspend fun getPayments() {
    val payments = database.paymentDao.query().map { it.toPaymentUiModel() }.take(3)
    _state.update { it.copy(payments = payments) }
  }

  private suspend fun getPaymentPlans() {
    val paymentPlans = database.paymentPlanDao.query().map { it.toPaymentPlanUiModel() }
    _state.update { it.copy(paymentPlans = paymentPlans) }
  }

  private fun onInvoice(event: HomeEvent.Invoice) {
    invoiceToPdf(
      context = application,
      account = state.value.accounts.first(),
      accountContact = state.value.accountContacts.first { it.primary },
      payment = event.payment,
      paymentPlan = state.value.paymentPlans.first(),
      company = state.value.company.first(),
      companyContact = state.value.companyContacts.first { it.primary },
    )
  }

  fun onEvent(event: HomeEvent) {
    when (event) {
      is HomeEvent.Invoice -> onInvoice(event)
    }
  }
}
