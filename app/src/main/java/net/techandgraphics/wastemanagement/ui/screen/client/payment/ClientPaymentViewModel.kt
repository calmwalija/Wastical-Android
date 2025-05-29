package net.techandgraphics.wastemanagement.ui.screen.client.payment

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentCacheEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentApi
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
import net.techandgraphics.wastemanagement.getUCropFile
import net.techandgraphics.wastemanagement.image2Text
import net.techandgraphics.wastemanagement.onTextToClipboard
import net.techandgraphics.wastemanagement.toBitmap
import net.techandgraphics.wastemanagement.toSoftwareBitmap
import net.techandgraphics.wastemanagement.worker.schedulePaymentRetryWorker
import javax.inject.Inject

@HiltViewModel
class ClientPaymentViewModel @Inject constructor(
  private val api: PaymentApi,
  private val application: Application,
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow(ClientPaymentState())
  private val _channel = Channel<ClientPaymentChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onAppState(event: ClientPaymentEvent.AppState) {
    _state.update { it.copy(state = event.state) }
  }

  private suspend fun getLastPaymentId() {
    database.paymentDao.getLastId()
      .collectLatest { lastPaymentId ->
        _state.update { it.copy(lastPaymentId = (lastPaymentId ?: 1L).plus(3)) }
        Log.e("TAG", "collectLatest : lastPaymentId " + _state.value.lastPaymentId)
      }
  }

  private fun theFile() = application.getUCropFile(_state.value.lastPaymentId)

  init {
    viewModelScope.launch {
      getLastPaymentId()
    }
  }

  val state = _state
    .onStart {
    }
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Companion.WhileSubscribed(5_000L),
      initialValue = ClientPaymentState(),
    )

  private fun onPay() = viewModelScope.launch {
    with(state.value) {
      val paymentRequest = PaymentRequest(
        screenshotText = screenshotText,
        paymentMethodId = state.paymentMethods.first().id,
        accountId = database.accountDao.query().first().id,
        numberOfMonths = numberOfMonths,
      )

      /** Pay by cash creates a dummy File **/
      state.paymentMethods
        .filter { it.type == PaymentType.Cash }
        .any { it.isSelected.not() }
        .also { theFile().createNewFile() }

      Log.e("TAG", "Before send : lastPaymentId " + lastPaymentId)

      runCatching { api.pay(theFile(), paymentRequest) }
        .onFailure {
          application.schedulePaymentRetryWorker()

          val plan = state.paymentPlans.first()
          val method = state.paymentMethods.first { it.isSelected }
          val gateway = state.paymentGateways.first { it.id == method.paymentGatewayId }

          val cachedPayment = paymentRequest.toPaymentCacheEntity(plan, gateway)

          Log.e("TAG", "onfail : lastPaymentId " + lastPaymentId)

          /** Rename the File **/
          val oldFile = application.getUCropFile(lastPaymentId)
          oldFile.renameTo(application.getUCropFile(cachedPayment.id))
          database.paymentDao.upsert(cachedPayment)
          _channel.send(ClientPaymentChannel.Pay.Failure(mapApiError(it)))
        }
        .onSuccess {
          database.paymentDao.upsert(it.toPaymentEntity())
          _channel.send(ClientPaymentChannel.Pay.Success)
          theFile().delete()
        }
      _state.update { it.copy(imageUri = null) }
    }
  }

  private fun onScreenshotAttached() = with(_state) {
    update { it.copy(screenshotAttached = true) }
    value.imageUri?.toBitmap(application)?.toSoftwareBitmap()?.run {
      image2Text {
        it.onSuccess { text -> _state.update { it.copy(screenshotText = text.trim()) } }
        it.onFailure(::println)
      }
    }
  }

  private fun onNumberOfMonths(event: ClientPaymentEvent.Button.NumberOfMonths) {
    if (event.isAdd) {
      state.value.numberOfMonths.plus(1)
    } else {
      state.value.numberOfMonths.minus(1)
    }.also { numberOfMonths -> _state.update { it.copy(numberOfMonths = numberOfMonths) } }
  }

  private fun onPaymentMethod(event: ClientPaymentEvent.Button.PaymentMethod) =
    viewModelScope.launch {
      state.value.state.paymentMethods.map { it.toPaymentMethodEntity() }
        .map { it.copy(isSelected = false) }
        .also { database.paymentMethodDao.update(it) }
      event.method.toPaymentMethodEntity()
        .copy(isSelected = !event.method.isSelected)
        .also { database.paymentMethodDao.update(it) }
    }

  fun onEvent(event: ClientPaymentEvent) {
    when (event) {
      is ClientPaymentEvent.Button.Pay -> onPay()
      is ClientPaymentEvent.Button.NumberOfMonths -> onNumberOfMonths(event)
      is ClientPaymentEvent.Button.TextToClipboard -> application.onTextToClipboard(event.text)
      is ClientPaymentEvent.Button.ImageUri -> _state.update { it.copy(imageUri = event.uri) }
      is ClientPaymentEvent.Button.ShowCropView -> _state.update { it.copy(showCropView = event.show) }
      ClientPaymentEvent.Button.ScreenshotAttached -> onScreenshotAttached()
      is ClientPaymentEvent.AppState -> onAppState(event)
      is ClientPaymentEvent.Button.PaymentMethod -> onPaymentMethod(event)
      else -> Unit
    }
  }
}
