package net.techandgraphics.wastemanagement.ui.screen.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.google.gson.Gson
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
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.onApiErrorHandler
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.domain.toPaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.image2Text
import net.techandgraphics.wastemanagement.onTextToClipboard
import net.techandgraphics.wastemanagement.toBitmap
import net.techandgraphics.wastemanagement.toSoftwareBitmap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
  private val api: AppApi,
  private val application: Application,
  private val database: AppDatabase,
  private val imageLoader: ImageLoader,
) : ViewModel() {

  private val _state = MutableStateFlow(PaymentState())
  private val _channel = Channel<PaymentChannel>()
  val channel = _channel.receiveAsFlow()

  private suspend fun getPaymentPlans() {
    database.paymentPlanDao.query().map { it.toPaymentPlanUiModel() }
      .also { paymentPlans ->
        _state.update { it.copy(paymentPlans = paymentPlans) }
      }
  }

  private suspend fun getLastPaymentId() {
    val lastPaymentId = database.paymentDao.getLastId() ?: 1
    _state.update { it.copy(lastPaymentId = lastPaymentId) }
  }

  private suspend fun getPaymentMethods() {
    database.paymentMethodDao.query().map { it.toPaymentMethodUiModel() }
      .also { paymentMethods ->
        _state.update { it.copy(paymentMethods = paymentMethods) }
      }
  }

  private fun theFile() = File(application.cacheDir, "${state.value.lastPaymentId}.jpg")

  init {
    viewModelScope.launch {
      getPaymentPlans()
      getPaymentMethods()
      getLastPaymentId()
    }
    _state.update { it.copy(imageLoader = imageLoader) }
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
    val paymentRequest = PaymentRequest(
      screenshotText = state.value.screenshotText,
      paymentMethodId = state.value.paymentMethods.first().id,
      accountId = database.accountDao.query().first().id,
      numberOfMonths = state.value.numberOfMonths,
    )

    val requestPart = Gson().toJson(paymentRequest).toRequestBody("application/json".toMediaType())
    val fileRequestBody = theFile().asRequestBody("application/octet-stream".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", theFile().name, fileRequestBody)

    runCatching { api.paymentApi.pay(filePart, requestPart) }
      .onFailure {
        database.paymentDao.upsert(paymentRequest.toPaymentCacheEntity())
        _channel.send(PaymentChannel.Pay.Failure(onApiErrorHandler(it)))
      }
      .onSuccess {
        database.paymentDao.upsert(it.toPaymentEntity())
        _channel.send(PaymentChannel.Pay.Success)
        theFile().delete()
      }
    _state.update { it.copy(imageUri = null) }
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
      else -> Unit
    }
  }
}
