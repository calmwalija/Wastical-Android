package net.techandgraphics.quantcal.ui.screen.client.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toPaymentMethodEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentRequestEntity
import net.techandgraphics.quantcal.data.remote.payment.PaymentRequest
import net.techandgraphics.quantcal.data.remote.payment.PaymentStatus
import net.techandgraphics.quantcal.domain.toAccountUiModel
import net.techandgraphics.quantcal.domain.toCompanyUiModel
import net.techandgraphics.quantcal.domain.toPaymentMethodWithGatewayAndPlanUiModel
import net.techandgraphics.quantcal.domain.toPaymentPlanUiModel
import net.techandgraphics.quantcal.onTextToClipboard
import javax.inject.Inject

@HiltViewModel
class ClientPaymentViewModel @Inject constructor(
  private val application: Application,
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow<ClientPaymentState>(ClientPaymentState.Loading)
  val state = _state.asStateFlow()

  private val _channel = Channel<ClientPaymentChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onLoad(event: ClientPaymentEvent.Load) = viewModelScope.launch {
    combine(
      flow = database.paymentMethodDao
        .flowOfWithGatewayAndPlan()
        .map { p0 -> p0.map { it.toPaymentMethodWithGatewayAndPlanUiModel() } },
      flow2 = database.accountDao
        .flowById(event.id)
        .mapNotNull { it?.toAccountUiModel() },
    ) { paymentMethods, account ->
      val company = database.companyDao.query().first().toCompanyUiModel()
      val accountPlan = database.accountPaymentPlanDao.getByAccountId(account.id)
      val paymentPlan =
        database.paymentPlanDao.get(accountPlan.paymentPlanId).toPaymentPlanUiModel()
      _state.value = ClientPaymentState.Success(
        company = company,
        account = account,
        paymentPlan = paymentPlan,
        paymentMethods = paymentMethods,
      )
    }.launchIn(this)
  }

  private fun onPay() = viewModelScope.launch {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      val paymentMethods = state.paymentMethods.map { it.method }
      val paymentMethod = paymentMethods.firstOrNull { it.isSelected } ?: paymentMethods.last()
      val paymentItem = state.paymentMethods.first { it.method.id == paymentMethod.id }
      val cachedPayment = PaymentRequest(
        paymentMethodId = paymentMethod.id,
        accountId = state.account.id,
        months = state.monthsCovered,
        companyId = state.company.id,
        executedById = state.account.id,
        createdAt = state.timestamp,
        status = PaymentStatus.Waiting,
      ).toPaymentRequestEntity()
      database.paymentRequestDao.upsert(cachedPayment)
      _state.value = state.copy(imageUri = null)
      _channel.send(ClientPaymentChannel.Pay.Success)
    }
  }

  private fun onScreenshotAttached() = {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(screenshotAttached = true)
    }
  }

  private fun onMonthCovered(event: ClientPaymentEvent.Button.MonthCovered) {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      val monthsCovered = when {
        event.isAdd -> state.monthsCovered.plus(1)
        else -> state.monthsCovered.minus(1)
      }
      _state.value = state.copy(monthsCovered = monthsCovered)
    }
  }

  private fun onPaymentMethod(event: ClientPaymentEvent.Button.PaymentMethod) =
    viewModelScope.launch {
      if (_state.value is ClientPaymentState.Success) {
        val state = (_state.value as ClientPaymentState.Success)
        state.paymentMethods
          .map { it.method }
          .map { it.toPaymentMethodEntity().copy(isSelected = false) }
          .also { database.paymentMethodDao.update(it) }
        event.method.copy(isSelected = true)
          .toPaymentMethodEntity()
          .also { database.paymentMethodDao.update(it) }
      }
    }

  private fun onImageUri(event: ClientPaymentEvent.Button.ImageUri) = {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(imageUri = event.uri)
    }
  }

  private fun onShowCropView(event: ClientPaymentEvent.Button.ShowCropView) = {
    if (_state.value is ClientPaymentState.Success) {
      val state = (_state.value as ClientPaymentState.Success)
      _state.value = state.copy(showCropView = event.show)
    }
  }

  fun onEvent(event: ClientPaymentEvent) {
    when (event) {
      is ClientPaymentEvent.Button.Pay -> onPay()
      is ClientPaymentEvent.Button.MonthCovered -> onMonthCovered(event)
      is ClientPaymentEvent.Button.TextToClipboard -> application.onTextToClipboard(event.text)
      is ClientPaymentEvent.Button.ImageUri -> onImageUri(event)
      is ClientPaymentEvent.Button.ShowCropView -> onShowCropView(event)
      ClientPaymentEvent.Button.ScreenshotAttached -> onScreenshotAttached()
      is ClientPaymentEvent.Load -> onLoad(event)
      is ClientPaymentEvent.Button.PaymentMethod -> onPaymentMethod(event)
      else -> Unit
    }
  }
}
