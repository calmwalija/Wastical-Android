package net.techandgraphics.quantcal.ui.screen.company.client.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.remote.mapApiError
import net.techandgraphics.quantcal.data.remote.payment.PaymentApi
import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.toAccountContactUiModel
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyContactUiModel
import net.techandgraphics.quantcal.domain.toCompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentGatewayUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodUiModel
import net.techandgraphics.quantcal.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentWithMonthsCoveredUiModel
import net.techandgraphics.quantcal.preview
import net.techandgraphics.quantcal.share
import net.techandgraphics.quantcal.ui.screen.client.invoice.pdf.invoiceToPdf
import net.techandgraphics.quantcal.ui.screen.company.client.history.CompanyClientHistoryEvent.Button
import net.techandgraphics.quantcal.ui.screen.company.client.history.CompanyClientHistoryEvent.Load
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CompanyClientHistoryViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
  private val api: PaymentApi,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyClientHistoryState>(CompanyClientHistoryState.Loading)
  val state = _state.asStateFlow()

  private fun getState() = (_state.value as CompanyClientHistoryState.Success)

  private fun onLoad(event: Load) =
    viewModelScope.launch {
      _state.value = CompanyClientHistoryState.Loading
      val account = database.accountDao.get(event.id).toAccountUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val company = database.companyDao.query().first().toCompanyUiModel()
      val plan = database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      val demographic = database.companyLocationDao.getWithDemographic(account.companyLocationId)
        .toCompanyLocationWithDemographicUiModel()
      _state.value = CompanyClientHistoryState.Success(
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
      with(_state.value as CompanyClientHistoryState.Success) {
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

  fun onEvent(event: CompanyClientHistoryEvent) {
    when (event) {
      is Load -> onLoad(event)
      is Button.Invoice.Event -> onEventInvoice(event)
      is Button.Delete -> Unit
      else -> Unit
    }
  }
}
