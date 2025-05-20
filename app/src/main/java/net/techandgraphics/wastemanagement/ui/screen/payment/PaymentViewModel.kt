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
import net.techandgraphics.wastemanagement.copyTextToClipboard
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.onApiErrorHandler
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.domain.toPaymentMethodUiModel
import net.techandgraphics.wastemanagement.domain.toPaymentPlanUiModel
import net.techandgraphics.wastemanagement.toFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
  private val appApi: AppApi,
  private val application: Application,
  private val database: AppDatabase,
  private val imageLoader: ImageLoader,
) : ViewModel() {

  private val paymentApi = appApi.paymentApi

  private val _state = MutableStateFlow(PaymentState())
  private val _channel = Channel<PaymentChannel>()
  val channel = _channel.receiveAsFlow()

  private suspend fun getPaymentPlans() {
    database.paymentPlanDao.query().map { it.toPaymentPlanUiModel() }
      .also { paymentPlans ->
        _state.update { it.copy(paymentPlans = paymentPlans) }
      }
  }

  private suspend fun getPaymentMethods() {
    database.paymentMethodDao.query().map { it.toPaymentMethodUiModel() }
      .also { paymentMethods ->
        _state.update { it.copy(paymentMethods = paymentMethods) }
      }
  }

  init {
    viewModelScope.launch {
      getPaymentPlans()
      getPaymentMethods()
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

  private fun onPay(event: PaymentEvent.Button.Pay) = viewModelScope.launch {
    val file = state.value.bitmapImage!!.toFile(application)

    val requestPart = Gson().toJson(
      PaymentRequest(
        transRef = event.message.trim(),
        paymentMethodId = 1L,
        accountId = 1L,
        numberOfMonths = 3,
      ),
    ).toRequestBody("application/json".toMediaType())

    val fileRequestBody = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
    val filePart = MultipartBody.Part.createFormData("file", file.name, fileRequestBody)

    runCatching { paymentApi.pay(filePart, requestPart) }
      .onFailure { onApiErrorHandler(it) }
      .onSuccess {
//        appDatabase.paymentDao.insert(it.toPaymentEntity())
      }

    _state.update { it.copy(bitmapImage = null) }
  }

  private fun onTextToClipboard(event: PaymentEvent.Button.TextToClipboard) {
    application.copyTextToClipboard(event.text)
  }

  fun onEvent(event: PaymentEvent) {
    when (event) {
      is PaymentEvent.Button.Pay -> onPay(event)
      is PaymentEvent.Button.ImageBitmap -> _state.update { it.copy(bitmapImage = event.bitmap) }

      is PaymentEvent.Button.NumberOfMonths -> onNumberOfMonths(event)
      is PaymentEvent.Button.TextToClipboard -> onTextToClipboard(event)
      else -> TODO("Handle actions")
    }
  }

  private fun onNumberOfMonths(event: PaymentEvent.Button.NumberOfMonths) {
    if (event.isAdd) {
      state.value.numberOfMonths.plus(1)
    } else {
      state.value.numberOfMonths.minus(1)
    }.also { numberOfMonths -> _state.update { it.copy(numberOfMonths = numberOfMonths) } }
  }
}
