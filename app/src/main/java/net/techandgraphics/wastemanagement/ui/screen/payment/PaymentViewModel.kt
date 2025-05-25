package net.techandgraphics.wastemanagement.ui.screen.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toPaymentCacheEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.remote.onApiErrorHandler
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentRepository
import net.techandgraphics.wastemanagement.getUCropFile
import net.techandgraphics.wastemanagement.image2Text
import net.techandgraphics.wastemanagement.onTextToClipboard
import net.techandgraphics.wastemanagement.toBitmap
import net.techandgraphics.wastemanagement.toSoftwareBitmap
import net.techandgraphics.wastemanagement.worker.schedulePaymentRetryWorker
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
  private val repository: PaymentRepository,
  private val application: Application,
  private val database: AppDatabase,
) : ViewModel() {

  private val _state = MutableStateFlow(PaymentState())
  private val _channel = Channel<PaymentChannel>()
  val channel = _channel.receiveAsFlow()

  private fun onAppState(event: PaymentEvent.AppState) {
    _state.update { it.copy(state = event.state) }
  }

  private suspend fun getLastPaymentId() {
    val lastPaymentId = database.paymentDao.getLastId() ?: 1
    _state.update { it.copy(lastPaymentId = lastPaymentId) }
  }

  private fun theFile() = application.getUCropFile(state.value.lastPaymentId)

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
      started = SharingStarted.WhileSubscribed(5_000L),
      initialValue = PaymentState(),
    )

  private fun onPay() = viewModelScope.launch {
    with(state.value) {
      val paymentRequest = PaymentRequest(
        screenshotText = screenshotText,
        paymentMethodId = state.paymentMethods.first().id,
        accountId = database.accountDao.query().first().id,
        numberOfMonths = numberOfMonths,
      )

      runCatching { repository.onPay(theFile(), paymentRequest) }
        .onFailure {
          application.schedulePaymentRetryWorker()
          val cachedPayment = paymentRequest.toPaymentCacheEntity()

          /** Rename the File **/
          val oldFile = application.getUCropFile(lastPaymentId)
          oldFile.renameTo(application.getUCropFile(cachedPayment.id))
          database.paymentDao.upsert(cachedPayment)
          _channel.send(PaymentChannel.Pay.Failure(onApiErrorHandler(it)))
        }
        .onSuccess {
          database.paymentDao.upsert(it.toPaymentEntity())
          _channel.send(PaymentChannel.Pay.Success)
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

  private fun onNumberOfMonths(event: PaymentEvent.Button.NumberOfMonths) {
    if (event.isAdd) {
      state.value.numberOfMonths.plus(1)
    } else {
      state.value.numberOfMonths.minus(1)
    }.also { numberOfMonths -> _state.update { it.copy(numberOfMonths = numberOfMonths) } }
  }

  fun onEvent(event: PaymentEvent) {
    when (event) {
      is PaymentEvent.Button.Pay -> onPay()
      is PaymentEvent.Button.NumberOfMonths -> onNumberOfMonths(event)
      is PaymentEvent.Button.TextToClipboard -> application.onTextToClipboard(event.text)
      is PaymentEvent.Button.ImageUri -> _state.update { it.copy(imageUri = event.uri) }
      is PaymentEvent.Button.ShowCropView -> _state.update { it.copy(showCropView = event.show) }
      PaymentEvent.Button.ScreenshotAttached -> onScreenshotAttached()
      is PaymentEvent.AppState -> onAppState(event)
      else -> Unit
    }
  }
}
