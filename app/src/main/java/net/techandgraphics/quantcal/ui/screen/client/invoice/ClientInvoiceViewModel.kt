package net.techandgraphics.quantcal.ui.screen.client.invoice

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.relations.toEntity
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.toAccountContactUiModel
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyContactUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.quantcal.preview
import net.techandgraphics.quantcal.share
import net.techandgraphics.quantcal.ui.screen.client.invoice.pdf.invoiceToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClientInvoiceViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientInvoiceState>(ClientInvoiceState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientInvoiceChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: ClientInvoiceEvent.Load) = viewModelScope.launch {
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
        database
          .paymentDao
          .qPaymentWithAccountAndMethodWithGateway(PaymentStatus.Approved.name)
          .map { p0 ->
            p0.map {
              it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
            }
          }
          .collectLatest { invoices ->
            _state.value = ClientInvoiceState.Success(
              invoices = invoices,
              company = company,
              account = account,
              paymentPlan = paymentPlan,
              paymentMethods = paymentMethods,
              accountContacts = accountContacts,
              companyContacts = companyContacts,
            )
          }
      }
  }

  private fun onInvoiceToPdf(payment: PaymentUiModel, onEvent: (File?) -> Unit) =
    viewModelScope.launch {
      if (_state.value is ClientInvoiceState.Success) {
        val state = (_state.value as ClientInvoiceState.Success)
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

  private fun onPaymentShare(event: ClientInvoiceEvent.Button.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  private fun onInvoice(event: ClientInvoiceEvent.Button.Invoice) {
    onInvoiceToPdf(event.payment) { file ->
      file?.preview(application)
    }
  }

  fun onEvent(event: ClientInvoiceEvent) {
    when (event) {
      is ClientInvoiceEvent.Load -> onLoad(event)
      is ClientInvoiceEvent.Button.Share -> onPaymentShare(event)
      is ClientInvoiceEvent.Button.Invoice -> onInvoice(event)

      else -> Unit
    }
  }
}
