package net.techandgraphics.wastical.ui.screen.client.payment

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
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.toPaymentRequestEntity
import net.techandgraphics.wastical.data.remote.account.HttpOperation
import net.techandgraphics.wastical.data.remote.payment.PaymentRequest
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.domain.toAccountUiModel
import net.techandgraphics.wastical.domain.toCompanyUiModel
import net.techandgraphics.wastical.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.wastical.domain.toPaymentPlanUiModel
import net.techandgraphics.wastical.getUCropFile
import net.techandgraphics.wastical.paymentReference
import net.techandgraphics.wastical.worker.client.payment.scheduleClientPaymentRequestWorker
import javax.inject.Inject

@HiltViewModel class ClientPaymentViewModel @Inject constructor(
  private val application: Application,
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientPaymentState>(ClientPaymentState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientPaymentChannel>()
  val channel = _channel.receiveAsFlow()
  private var recordPaymentJob: Job? = null

  private fun onLoad(event: ClientPaymentEvent.Load) = viewModelScope.launch {
    database.accountDao.flowById(event.id).mapNotNull { it?.toAccountUiModel() }
      .collectLatest { account ->
        val company = database.companyDao.query().first().toCompanyUiModel()
        val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
        val paymentPlan =
          database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
        database.paymentMethodDao.flowOfWithGatewayAndPlan()
          .map { p0 -> p0.map { it.toPaymentMethodWithGatewayAndPlanUiModel() } }
          .collectLatest { paymentMethods ->
            _state.value = ClientPaymentState.Success(
              company = company,
              account = account,
              paymentMethods = paymentMethods,
              paymentPlan = paymentPlan,
            )
          }
      }
  }

  private fun onSubmit() {
    recordPaymentJob?.cancel()
    recordPaymentJob = viewModelScope.launch {
      delay(5_00)
      if (_state.value is ClientPaymentState.Success) {
        val state = (_state.value as ClientPaymentState.Success)
        val paymentMethods = state.paymentMethods.map { it.method }
        val paymentMethod = paymentMethods.firstOrNull { it.isSelected } ?: paymentMethods.last()
        val cachedPayment = PaymentRequest(
          paymentMethodId = paymentMethod.id,
          accountId = state.account.id,
          months = state.monthsCovered,
          companyId = state.company.id,
          executedById = state.account.id,
          status = PaymentStatus.Waiting,
          httpOperation = HttpOperation.Post.name,
          paymentReference = paymentReference(),
        ).toPaymentRequestEntity()
        database.paymentRequestDao.upsert(cachedPayment)
        val newPayment = database.paymentRequestDao.getLast()
        val oldFile = application.getUCropFile(state.timestamp)
        oldFile.renameTo(application.getUCropFile(newPayment.createdAt))
        application.scheduleClientPaymentRequestWorker()
        _state.value = state.copy(imageUri = null)
        _channel.send(ClientPaymentChannel.Pay.Success)
      }
    }
  }

  private fun onScreenshotAttached() {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(screenshotAttached = true)
    }
  }

  private fun onMonthCovered(event: ClientPaymentEvent.Button.MonthCovered) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      val monthsCovered = when {
        event.isAdd -> state.monthsCovered.plus(1).coerceAtMost(12)
        else -> state.monthsCovered.minus(1).coerceAtLeast(1)
      }
      _state.value = state.copy(monthsCovered = monthsCovered)
    }
  }

  private fun onPaymentMethod(event: ClientPaymentEvent.Button.PaymentMethod) =
    viewModelScope.launch {
      if (_state.value is ClientPaymentState.Success) {
        val state = (_state.value as ClientPaymentState.Success)
        state.paymentMethods.map { it.method }
          .map { it.toPaymentMethodEntity().copy(isSelected = false) }
          .also { database.paymentMethodDao.update(it) }
        event.item.method.copy(isSelected = true).toPaymentMethodEntity()
          .also { database.paymentMethodDao.update(it) }
      }
    }

  private fun onImageUri(event: ClientPaymentEvent.Button.ImageUri) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(imageUri = event.uri)
    }
  }

  private fun onShowCropView(event: ClientPaymentEvent.Button.ShowCropView) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(showCropView = event.show)
    }
  }

  fun onEvent(event: ClientPaymentEvent) {
    when (event) {
      ClientPaymentEvent.Button.Submit -> onSubmit()
      is ClientPaymentEvent.Button.MonthCovered -> onMonthCovered(event)
      is ClientPaymentEvent.Button.ImageUri -> onImageUri(event)
      is ClientPaymentEvent.Button.ShowCropView -> onShowCropView(event)
      ClientPaymentEvent.Button.ScreenshotAttached -> onScreenshotAttached()
      is ClientPaymentEvent.Load -> onLoad(event)
      is ClientPaymentEvent.Button.PaymentMethod -> onPaymentMethod(event)
      else -> Unit
    }
  }
}
