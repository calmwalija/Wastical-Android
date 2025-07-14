package net.techandgraphics.quantcal.ui.screen.client.home

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.toAccountContactUiModel
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyBinCollectionUiModel
import net.techandgraphics.quantcal.domain.toCompanyContactUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentWithMonthsCoveredUiModel
import net.techandgraphics.quantcal.onTextToClipboard
import net.techandgraphics.quantcal.preview
import net.techandgraphics.quantcal.share
import net.techandgraphics.quantcal.ui.screen.client.invoice.pdf.invoiceToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel class ClientHomeViewModel @Inject constructor(
  private val application: Application,
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientHomeState>(ClientHomeState.Loading)
  private val _channel = Channel<ClientHomeChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()

  private fun onLoad(event: ClientHomeEvent.Load) = viewModelScope.launch {
    database.accountDao.flowById(event.id)
      .mapNotNull { it?.toAccountUiModel() }
      .collectLatest { account ->
        val accountContacts = database.accountContactDao
          .getByAccountId(event.id)
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
        combine(
          flow = database
            .paymentDao
            .flowOfInvoicesWithMonthCovered()
            .map { p0 -> p0.map { it.toPaymentWithMonthsCoveredUiModel() } },
          flow2 = database
            .paymentDao
            .flowOfPaymentsWithMonthCovered()
            .map { p0 -> p0.map { it.toPaymentWithMonthsCoveredUiModel() } },
        ) { invoices, payments ->
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

  private fun onPaymentShare(event: ClientHomeEvent.Button.Payment.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  fun onEvent(event: ClientHomeEvent) {
    when (event) {
      is ClientHomeEvent.Button.Payment.Invoice -> onPaymentTap(event)
      is ClientHomeEvent.Button.Payment.Share -> onPaymentShare(event)
      is ClientHomeEvent.Load -> onLoad(event)
      is ClientHomeEvent.Button.Payment.TextToClipboard -> application.onTextToClipboard(event.text)
      else -> Unit
    }
  }
}
