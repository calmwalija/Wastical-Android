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
import net.techandgraphics.wastemanagement.domain.toAccountUiModel
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentUiModel
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
        state = event.state,
      )
      launch { getPayments(account) }
    }

  private fun getPayments(account: AccountUiModel) = viewModelScope.launch {
    database.paymentDao.flowOfByAccountId(account.id)
      .map { flowOf -> flowOf.map { it.toPaymentUiModel() } }
      .collectLatest { payments -> _state.value = getState().copy(payments = payments) }
  }

  private fun onInvoiceToPdf(payment: PaymentUiModel, onEvent: (File?) -> Unit) =
    with(state.value as CompanyClientHistoryState.Success) {
      val paymentMethod = state.paymentMethods.first { it.id == payment.paymentMethodId }
      val paymentGateway = state.paymentGateways.first { it.id == paymentMethod.id }
      invoiceToPdf(
        context = application,
        account = state.accounts.first(),
        accountContact = state.accountContacts.first { it.primary },
        payment = payment,
        paymentPlan = plan,
        company = state.companies.first(),
        companyContact = state.companyContacts.first { it.primary },
        paymentMethod = paymentMethod,
        onEvent = onEvent,
        paymentGateway = paymentGateway,
      )
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
