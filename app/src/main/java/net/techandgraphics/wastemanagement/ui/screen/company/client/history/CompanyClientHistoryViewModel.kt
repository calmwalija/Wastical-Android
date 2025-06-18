package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.domain.toAccountContactUiModel
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyContactUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentGatewayUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentWithMonthsCoveredUiModel
import net.techandgraphics.wastemanagement.preview
import net.techandgraphics.wastemanagement.share
import net.techandgraphics.wastemanagement.ui.screen.client.invoice.pdf.invoiceToPdf
import net.techandgraphics.wastemanagement.ui.screen.company.client.history.CompanyClientHistoryEvent.Button
import net.techandgraphics.wastemanagement.ui.screen.company.client.history.CompanyClientHistoryEvent.Load
import java.io.File
import javax.inject.Inject

@HiltViewModel
class CompanyClientHistoryViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
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
      _state.value = CompanyClientHistoryState.Success(
        company = company,
        account = account,
        plan = plan,
      )
      launch { getPayments(account) }
    }

  private fun getPayments(account: AccountUiModel) = viewModelScope.launch {
    database.paymentDao.flowOfWithMonthCoveredByAccountId(account.id)
      .map { flowOf -> flowOf.map { it.toPaymentWithMonthsCoveredUiModel() } }
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

  fun onEvent(event: CompanyClientHistoryEvent) {
    when (event) {
      is Load -> onLoad(event)
      is Button.Invoice.Event -> onEventInvoice(event)
    }
  }
}
