package net.techandgraphics.wastical.ui.screen.company.client.history

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
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.data.remote.payment.PaymentApi
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.data.remote.toPaymentResponse
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
import net.techandgraphics.wastical.ui.screen.client.invoice.pdf.invoiceToPdf
import net.techandgraphics.wastical.ui.screen.company.client.history.CompanyPaymentHistoryEvent.Button
import net.techandgraphics.wastical.ui.screen.company.client.history.CompanyPaymentHistoryEvent.Load
import net.techandgraphics.wastical.worker.company.payment.scheduleCompanyPaymentRequestWorker
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CompanyPaymentHistoryViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
  private val api: PaymentApi,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyPaymentHistoryState>(CompanyPaymentHistoryState.Loading)
  val state = _state.asStateFlow()

  private fun onLoad(event: Load) =
    viewModelScope.launch {
      _state.value = CompanyPaymentHistoryState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val company = database.companyDao.query().first().toCompanyUiModel()
      val plan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
        .toCompanyLocationWithDemographicUiModel()
      database.paymentDao.flowOfWithMonthCoveredByAccountId(account.id)
        .map { flowOf -> flowOf.map { it.toPaymentWithMonthsCoveredUiModel() } }
        .collectLatest { payments ->
          _state.value = CompanyPaymentHistoryState.Success(
            company = company,
            account = account,
            plan = plan,
            payments = payments,
            demographic = demographic,
          )
        }
    }

  private fun onInvoiceToPdf(payment: PaymentUiModel, onEvent: (File?) -> Unit) =
    viewModelScope.launch {
      with(_state.value as CompanyPaymentHistoryState.Success) {
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

  private fun onEventButtonDelete(event: Button.Delete) =
    viewModelScope.launch {
      runCatching { api.delete(event.id) }
        .onSuccess { database.paymentDao.delete(database.paymentDao.get(it)) }
        .onFailure { println(mapApiError(it)) }
    }

  private fun onPaymentDeny(event: CompanyPaymentHistoryEvent.Payment.Deny) =
    viewModelScope.launch {
      val cachePayment =
        event.payment
          .toPaymentEntity()
          .toPaymentRequestEntity(httpOperation = HttpOperation.Put)
          .copy(status = PaymentStatus.Declined.name)
      onCachePayment(cachePayment)
    }

  private suspend fun onCachePayment(cachePayment: PaymentRequestEntity) {
    database.paymentRequestDao.insert(cachePayment)
    cachePayment.toPaymentResponse().toPaymentEntity().also { payment ->
      database.paymentDao.update(payment)
    }
    application.scheduleCompanyPaymentRequestWorker()
  }

  private fun onPaymentApprove(event: CompanyPaymentHistoryEvent.Payment.Approve) =
    viewModelScope.launch {
      val cachePayment =
        event.payment
          .toPaymentEntity()
          .toPaymentRequestEntity(httpOperation = HttpOperation.Put)
          .copy(status = PaymentStatus.Approved.name)
      onCachePayment(cachePayment)
    }

  fun onEvent(event: CompanyPaymentHistoryEvent) {
    when (event) {
      is Load -> onLoad(event)
      is Button.Invoice.Event -> onEventInvoice(event)
      is CompanyPaymentHistoryEvent.Payment.Approve -> onPaymentApprove(event)
      is CompanyPaymentHistoryEvent.Payment.Deny -> onPaymentDeny(event)
      else -> Unit
    }
  }
}
