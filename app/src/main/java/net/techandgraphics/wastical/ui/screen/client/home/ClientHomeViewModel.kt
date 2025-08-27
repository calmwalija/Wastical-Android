package net.techandgraphics.wastical.ui.screen.client.home

import android.accounts.AccountManager
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.account.session.AccountSessionRepository
import net.techandgraphics.wastical.data.local.database.dashboard.payment.MonthYear
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.domain.toAccountContactUiModel
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyBinCollectionUiModel
import net.techandgraphics.wastical.domain.toCompanyContactUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentGatewayUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentMonthCoveredUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentRequestWithAccountUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.getAccount
import net.techandgraphics.wastical.onTextToClipboard
import net.techandgraphics.wastical.preview
import net.techandgraphics.wastical.share
import net.techandgraphics.wastical.toZonedDateTime
import net.techandgraphics.wastical.ui.screen.AccountLogout
import net.techandgraphics.wastical.ui.screen.client.invoice.pdf.invoiceToPdf
import net.techandgraphics.wastical.worker.LAST_UPDATED_WORKER_UUID
import net.techandgraphics.wastical.worker.scheduleAccountLastUpdatedWorker
import java.io.File
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@HiltViewModel class ClientHomeViewModel @Inject constructor(
  private val application: Application,
  private val database: AppDatabase,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val workManager: WorkManager,
  private val accountSession: AccountSessionRepository,
  private val accountLogout: AccountLogout,
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
      ?.let { internalAccount ->
        if (database.companyDao.query().isEmpty()) {
          _channel.send(ClientHomeChannel.Goto.Reload)
          return@let
        }
        val account = database.accountDao.get(internalAccount.id).toAccountUiModel()
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
        // Compute months outstanding similar to ClientPaymentViewModel
        val today = ZonedDateTime.now()
        val lastCovered = database.paymentMonthCoveredDao.getLastByAccount(account.id)
        val aging = database.paymentIndicatorDao.qAgingRawByAccountId(account.id)

        val monthsOutstanding = if (aging != null) {
          val createdZdt = aging.createdAt.toZonedDateTime()
          val startYm = YearMonth.of(createdZdt.year, createdZdt.month)
          val billingYm = if (today.dayOfMonth >= company.billingDate) {
            YearMonth.of(today.year, today.month)
          } else {
            YearMonth.of(today.year, today.month).minusMonths(1)
          }
          val targetYm = billingYm.plusMonths(1)
          val lastCoveredYm = lastCovered?.let { YearMonth.of(it.year, it.month) }
          val firstDueYm = lastCoveredYm?.plusMonths(1) ?: startYm.plusMonths(1)
          if (firstDueYm.isAfter(targetYm)) {
            0
          } else {
            (ChronoUnit.MONTHS.between(firstDueYm.atDay(1), targetYm.atDay(1)).toInt() + 1)
              .coerceAtLeast(0)
          }
        } else {
          0
        }

        val outstandingMonths: List<MonthYear> = if (aging != null && monthsOutstanding > 0) {
          val createdZdt = aging.createdAt.toZonedDateTime()
          val startYm = YearMonth.of(createdZdt.year, createdZdt.month)
          val lastCoveredYm = lastCovered?.let { YearMonth.of(it.year, it.month) }
          val firstDueYm = lastCoveredYm?.plusMonths(1) ?: startYm.plusMonths(1)
          (0 until monthsOutstanding).map { idx ->
            val ym = firstDueYm.plusMonths(idx.toLong())
            MonthYear(ym.month.value, ym.year)
          }
        } else {
          emptyList()
        }

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
            .qPaymentWithAccountAndMethodWithGateway(PaymentStatus.Verifying.name)
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
          val lastMonthCovered = lastCovered?.toPaymentMonthCoveredUiModel()
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
            monthsOutstanding = monthsOutstanding,
            outstandingMonths = outstandingMonths,
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
    if (_state.value is ClientHomeState.Success) {
      observeAccountSessionWorker()
      application.scheduleAccountLastUpdatedWorker()
    }
  }

  private fun observeAccountSessionWorker() {
    viewModelScope.launch {
      workManager.getWorkInfosForUniqueWorkFlow(LAST_UPDATED_WORKER_UUID)
        .mapNotNull { workInfoList -> workInfoList.firstOrNull() }
        .collect { workInfo ->
          when (workInfo.state) {
            WorkInfo.State.SUCCEEDED -> {
              delay(1_000)
              _channel.send(ClientHomeChannel.Fetch.Success)
            }

            else -> Unit
          }
        }
    }
  }

  private fun onRefetchInfo() = viewModelScope.launch {
    val account = authenticatorHelper.getAccount(accountManager) ?: return@launch
    runCatching { accountSession.fetch(account.id) }.onSuccess { data ->
      try {
        database.withTransaction {
          database.clearAllTables()
          accountSession.purseData(data) { _, _ -> }
        }
        _channel.send(ClientHomeChannel.Fetch.Success)
      } catch (e: Exception) {
        e.printStackTrace()
      }
    }.onFailure { it.printStackTrace() }
  }

  private fun onPaymentShare(event: ClientHomeEvent.Button.Payment.Share) {
    onInvoiceToPdf(event.payment) { file ->
      file?.share(application)
    }
  }

  private fun onLogout() = viewModelScope.launch {
    accountLogout.invoke()
      .onSuccess { _channel.send(ClientHomeChannel.Goto.Login) }
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
