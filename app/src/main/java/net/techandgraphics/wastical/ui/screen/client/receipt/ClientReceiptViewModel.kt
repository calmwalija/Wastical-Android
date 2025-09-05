package net.techandgraphics.wastical.ui.screen.client.receipt

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
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.domain.toAccountContactUiModel
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyContactUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toDemographicAreaUiModel
import net.techandgraphics.wastical.domain.toDemographicStreetUiModel
import net.techandgraphics.wastical.domain.toPaymentGatewayUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.preview
import net.techandgraphics.wastical.share
import net.techandgraphics.wastical.ui.receipt.pdf.receiptToPdf
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClientReceiptViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientReceiptState>(ClientReceiptState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientReceiptChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: ClientReceiptEvent.Load) = viewModelScope.launch {
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
            _state.value = ClientReceiptState.Success(
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
      if (_state.value is ClientReceiptState.Success) {
        val state = (_state.value as ClientReceiptState.Success)
        val paymentMethod = database.paymentMethodDao.get(payment.paymentMethodId)
          .toPaymentMethodUiModel()
        val paymentGateway = database.paymentGatewayDao.get(paymentMethod.paymentGatewayId)
          .toPaymentGatewayUiModel()
        val paymentMonthCovered = database.paymentMonthCoveredDao.getByPaymentId(payment.id)
          .map { it.toPaymentMonthCoveredUiModel() }
        val companyLocation = database.companyLocationDao.getByCompanyId(state.company.id)
        val demographicArea = database.demographicAreaDao.get(companyLocation.demographicAreaId)
          .toDemographicAreaUiModel()
        val demographicStreet = database.demographicStreetDao.get(companyLocation.demographicStreetId)
          .toDemographicStreetUiModel()
        receiptToPdf(
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
          demographicStreet = demographicStreet,
          demographicArea = demographicArea,
        )
      }
    }

  private fun onPaymentShare(event: ClientReceiptEvent.Button.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  private fun onInvoice(event: ClientReceiptEvent.Button.Invoice) {
    onInvoiceToPdf(event.payment) { file ->
      file?.preview(application)
    }
  }

  fun onEvent(event: ClientReceiptEvent) {
    when (event) {
      is ClientReceiptEvent.Load -> onLoad(event)
      is ClientReceiptEvent.Button.Share -> onPaymentShare(event)
      is ClientReceiptEvent.Button.Invoice -> onInvoice(event)

      else -> Unit
    }
  }
}
