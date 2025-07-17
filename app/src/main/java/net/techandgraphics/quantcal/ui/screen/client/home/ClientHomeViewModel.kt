package net.techandgraphics.quantcal.ui.screen.client.home

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.account.AuthenticatorHelper
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.quantcal.data.local.database.relations.toEntity
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.toAccountContactUiModel
import net.techandgraphics.quantcal.domain.toCompanyBinCollectionUiModel
import net.techandgraphics.quantcal.domain.toCompanyContactUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentRequestWithAccountUiModel
import net.techandgraphics.quantcal.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.getAccount
import net.techandgraphics.quantcal.onTextToClipboard
import net.techandgraphics.quantcal.preview
import net.techandgraphics.quantcal.share
import net.techandgraphics.quantcal.ui.screen.client.invoice.pdf.invoiceToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel class ClientHomeViewModel @Inject constructor(
  private val application: Application,
  private val database: AppDatabase,
  private val accountSession: AccountSessionRepository,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientHomeState>(ClientHomeState.Loading)
  private val _channel = Channel<ClientHomeChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()

  init {
    onEvent(ClientHomeEvent.Load)
  }

  private fun onLoad() = viewModelScope.launch {
    authenticatorHelper.getAccount(accountManager)
      ?.let { account ->
        val accountContacts = database.accountContactDao
          .getByAccountId(account.id)
          .map { it.toAccountContactUiModel() }
        val company = database.companyDao.query().first().toCompanyUiModel()
        val companyContacts = database.companyContactDao
          .query()
          .map { it.toCompanyContactUiModel() }
        val paymentMethods = database.paymentMethodDao.qWithGatewayAndPlan()
          .map { it.toPaymentMethodWithGatewayAndPlanUiModel() }
        val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
        val paymentPlan =
          database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
        val companyBinCollections = database.companyBinCollectionDao.query()
          .map { it.toCompanyBinCollectionUiModel() }
        val lastMonthCovered =
          database.paymentMonthCoveredDao.getLast()?.toPaymentMonthCoveredUiModel()
        combine(
          flow = database
            .paymentDao
            .qPaymentWithAccountAndMethodWithGatewayLimit(PaymentStatus.Approved.name, 4)
            .map { p0 ->
              p0.map {
                it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
              }
            },
          flow2 = database
            .paymentDao
            .qPaymentWithAccountAndMethodWithGatewayNot(PaymentStatus.Approved.name)
            .map { p0 ->
              p0.map {
                it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
              }
            },
          flow3 = database
            .paymentRequestDao
            .qFlowWithAccount()
            .map { p0 -> p0.map { it.toPaymentRequestWithAccountUiModel() } },
        ) { invoices, payments, paymentRequests ->
          _state.value = ClientHomeState.Success(
            invoices = invoices,
            payments = payments,
            company = company,
            account = account,
            paymentPlan = paymentPlan,
            paymentMethods = paymentMethods,
            accountContacts = accountContacts,
            companyContacts = companyContacts,
            companyBinCollections = companyBinCollections,
            lastMonthCovered = lastMonthCovered,
            paymentRequests = paymentRequests,
          )
        }.launchIn(this)
      }
  }

  private fun onInvoiceToPdf(payment: PaymentUiModel, onEvent: (File?) -> Unit) =
    viewModelScope.launch {
      if (_state.value is ClientHomeState.Success) {
        val state = (_state.value as ClientHomeState.Success)
        val paymentMethod = database.paymentMethodDao.get(payment.paymentMethodId)
          .toPaymentMethodUiModel()
        val paymentGateway = database.paymentGatewayDao.get(paymentMethod.paymentGatewayId)
          .toPaymentGatewayUiModel()
        val paymentMonthCovered = database.paymentMonthCoveredDao.getByPaymentId(payment.id)
          .map { it.toPaymentMonthCoveredUiModel() }
        invoiceToPdf(
          context = application,
          account = state.account,
          accountContact = state.accountContacts.first { it.primary },
          payment = payment,
          paymentPlan = state.paymentPlan,
          company = state.company,
          companyContact = state.companyContacts.first { it.primary },
          paymentMethod = paymentMethod,
          onEvent = onEvent,
          paymentGateway = paymentGateway,
          paymentMonthCovered = paymentMonthCovered,
        )
      }
    }

  private fun onPaymentTap(event: ClientHomeEvent.Button.Payment.Invoice) {
    when (event.payment.status) {
      PaymentStatus.Approved -> onInvoiceToPdf(event.payment) { file ->
        file?.preview(application)
      }

      else -> Unit
    }
  }

  private fun onFetchChanges() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager) ?: return@launch
    _channel.send(ClientHomeChannel.Fetch.Fetching)
    runCatching { accountSession.fetch(account.id) }
      .onSuccess { data ->
        try {
          database.withTransaction {
            database.clearAllTables()
            accountSession.purseData(data) { _, _ -> }
          }
          _channel.send(ClientHomeChannel.Fetch.Success)
        } catch (e: Exception) {
          _channel.send(ClientHomeChannel.Fetch.Error(mapApiError(e)))
        }
      }
      .onFailure { _channel.send(ClientHomeChannel.Fetch.Error(mapApiError(it))) }
  }

  private fun onPaymentShare(event: ClientHomeEvent.Button.Payment.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  private fun onLogout() = viewModelScope.launch(Dispatchers.IO) {
    authenticatorHelper.deleteAccounts()
    _channel.send(ClientHomeChannel.Goto.Login)
  }

  fun onEvent(event: ClientHomeEvent) {
    when (event) {
      is ClientHomeEvent.Button.Payment.Invoice -> onPaymentTap(event)
      is ClientHomeEvent.Button.Payment.Share -> onPaymentShare(event)
      ClientHomeEvent.Button.Fetch -> onFetchChanges()
      ClientHomeEvent.Load -> onLoad()
      ClientHomeEvent.Button.Logout -> onLogout()
      is ClientHomeEvent.Button.Payment.TextToClipboard -> application.onTextToClipboard(event.text)
      else -> Unit
    }
  }
}
