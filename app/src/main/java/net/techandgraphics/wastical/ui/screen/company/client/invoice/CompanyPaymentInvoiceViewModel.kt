package net.techandgraphics.wastical.ui.screen.company.client.invoice

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.domain.toAccountContactUiModel
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyContactUiModel
import net.techandgraphics.wastical.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentGatewayUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodUiModel
import net.techandgraphics.wastical.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentWithMonthsCoveredUiModel
import net.techandgraphics.wastical.preview
import net.techandgraphics.wastical.share
import net.techandgraphics.wastical.ui.invoice.pdf.invoiceToPdf
import net.techandgraphics.wastical.ui.screen.company.client.invoice.CompanyPaymentInvoiceEvent.Button
import net.techandgraphics.wastical.ui.screen.company.client.invoice.CompanyPaymentInvoiceEvent.Load
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentInvoiceViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentInvoiceState>(CompanyPaymentInvoiceState.Loading)
  val state = _state.asStateFlow()

  private fun getState() = (_state.value as CompanyPaymentInvoiceState.Success)

  private fun onLoad(event: Load) =
    viewModelScope.launch {
      _state.value = CompanyPaymentInvoiceState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val company = database.companyDao.query().first().toCompanyUiModel()
      val plan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
        .toCompanyLocationWithDemographicUiModel()
      _state.value = CompanyPaymentInvoiceState.Success(
        company = company,
        account = account,
        plan = plan,
        demographic = demographic,
      )
      launch { getPayments(account) }
    }

  private fun getPayments(account: AccountUiModel) = viewModelScope.launch {
    database.paymentDao.flowOfWithMonthCoveredByAccountId(account.id)
      .map { flowOf ->
        flowOf.map { it.toPaymentWithMonthsCoveredUiModel() }
          .sortedBy { it.payment.createdAt }
      }
      .collectLatest { payments -> _state.value = getState().copy(payments = payments) }
  }

  private fun onInvoiceToPdf(payment: PaymentUiModel, onEvent: (File?) -> Unit) =
    viewModelScope.launch {
      with(_state.value as CompanyPaymentInvoiceState.Success) {
        val accountContact = database.accountContactDao.getByAccountId(account.id)
          .map { it.toAccountContactUiModel() }
          .first()

        val paymentMethod = database.paymentMethodDao.get(payment.paymentMethodId)
          .toPaymentMethodUiModel()

        val paymentGateway = database.paymentGatewayDao.get(paymentMethod.paymentGatewayId)
          .toPaymentGatewayUiModel()

        val paymentPlan =
          database.paymentPlanDao.get(paymentMethod.paymentPlanId).toPaymentPlanUiModel()

        val companyContact = database.companyContactDao.query()
          .map { it.toCompanyContactUiModel() }
          .first { it.primary }

        val paymentMonthCovered = database.paymentMonthCoveredDao
          .getByPaymentId(payment.id)
          .map { it.toPaymentMonthCoveredUiModel() }

        invoiceToPdf(
          context = application,
          account = account,
          accountContact = accountContact,
          payment = payment,
          paymentPlan = paymentPlan,
          company = company,
          companyContact = companyContact,
          paymentMethod = paymentMethod,
          onEvent = onEvent,
          paymentGateway = paymentGateway,
          paymentMonthCovered = paymentMonthCovered,
        )
      }
    }

  private fun onEventInvoice(event: Button.Invoice.Event) {
    onInvoiceToPdf(event.payment) { file ->
      when (event.op) {
        Button.Invoice.Op.Preview -> file?.preview(application)
        Button.Invoice.Op.Share -> file?.share(application)
      }
    }
  }

  fun onEvent(event: CompanyPaymentInvoiceEvent) {
    when (event) {
      is Load -> onLoad(event)
      is Button.Invoice.Event -> onEventInvoice(event)
      else -> Unit
    }
  }
}
