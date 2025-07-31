package net.techandgraphics.wastical.ui.screen.company.payment.verify

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity
import net.techandgraphics.wastical.data.local.database.relations.toEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.data.remote.toPaymentResponse
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentWithAccountAndMethodWithGatewayUiModel
import net.techandgraphics.wastical.worker.company.payment.scheduleCompanyPaymentRequestWorker
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class CompanyVerifyPaymentViewModel @Inject constructor(
  private val database: AppDatabase,
  private val application: Application,
) : ViewModel() {

  private val _state =
    MutableStateFlow<CompanyVerifyPaymentState>(CompanyVerifyPaymentState.Loading)
  private val _channel = Channel<CompanyVerifyPaymentChannel>()
  val channel = _channel.receiveAsFlow()
  val state = _state.asStateFlow()
  private var searchJob: Job? = null

  private fun onLoad() = viewModelScope.launch {
    database.paymentDao
      .qPaymentWithAccountAndMethodWithGateway(PaymentStatus.Verifying.name)
      .map { fromDb ->
        fromDb.map {
          it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
        }
      }.collectLatest { payments ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        _state.value = CompanyVerifyPaymentState.Success(
          company = company,
          payments = payments,
        )
      }
  }

  private fun onInputSearch(event: CompanyVerifyPaymentEvent.Input.Search) {
    if (_state.value is CompanyVerifyPaymentState.Success) {
      _state.value = (_state.value as CompanyVerifyPaymentState.Success).copy(query = event.query)
      searchJob?.cancel()
      searchJob = viewModelScope.launch {
        delay(1_000)
        database.paymentDao
          .qPaymentWithAccountAndMethodWithGateway(
            query = event.query.trim(),
            status = PaymentStatus.Verifying.name,
          )
          .map { fromDb ->
            fromDb.map {
              it.toEntity().toPaymentWithAccountAndMethodWithGatewayUiModel()
            }
          }.collectLatest { payments ->
            _state.value =
              (_state.value as CompanyVerifyPaymentState.Success).copy(payments = payments)
          }
      }
    }
  }

  init {
    onEvent(CompanyVerifyPaymentEvent.Load)
  }

  private fun onPaymentDeny(event: CompanyVerifyPaymentEvent.Payment.Deny) =
    viewModelScope.launch {
      val cachePayment =
        event.payment
          .toPaymentEntity()
          .toPaymentRequestEntity(httpOperation = HttpOperation.Put)
          .copy(
            status = PaymentStatus.Declined.name,
            updatedAt = ZonedDateTime.now().toEpochSecond(),
          )
      onCachePayment(cachePayment)
    }

  private suspend fun onCachePayment(cachePayment: PaymentRequestEntity) {
    database.paymentRequestDao.insert(cachePayment)
    cachePayment.toPaymentResponse().toPaymentEntity().also { payment ->
      database.paymentDao.update(payment)
    }
    application.scheduleCompanyPaymentRequestWorker()
  }

  private fun onPaymentApprove(event: CompanyVerifyPaymentEvent.Payment.Approve) =
    viewModelScope.launch {
      val cachePayment =
        event.payment
          .toPaymentEntity()
          .toPaymentRequestEntity(httpOperation = HttpOperation.Put)
          .copy(
            status = PaymentStatus.Approved.name,
            updatedAt = ZonedDateTime.now().toEpochSecond(),
          )

      onCachePayment(cachePayment)
    }

  fun onEvent(event: CompanyVerifyPaymentEvent) {
    when (event) {
      is CompanyVerifyPaymentEvent.Load -> onLoad()
      is CompanyVerifyPaymentEvent.Payment.Approve -> onPaymentApprove(event)
      is CompanyVerifyPaymentEvent.Payment.Deny -> onPaymentDeny(event)
      is CompanyVerifyPaymentEvent.Input.Search -> onInputSearch(event)
      else -> Unit
    }
  }
}
