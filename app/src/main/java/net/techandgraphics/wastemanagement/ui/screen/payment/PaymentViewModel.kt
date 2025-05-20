package net.techandgraphics.wastemanagement.ui.screen.payment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonParseException
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.LoadingEvent
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import net.techandgraphics.wastemanagement.toFile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(
  private val appApi: AppApi,
  private val application: Application,
  private val appDatabase: AppDatabase,
) : ViewModel() {

  private val paymentApi = appApi.paymentApi

  private val _state = MutableStateFlow(PaymentState())
  private val _channel = Channel<PaymentChannel>()
  val channel = _channel.receiveAsFlow()

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
      .onFailure { throwable ->
        val onError: LoadingEvent.Error = when (throwable) {
          is IOException -> LoadingEvent.Error(
            "Network error. Please check your connection.",
            throwable,
          )

          is HttpException -> {
            val code = throwable.code()
            val message = when (code) {
              HttpStatusCode.Unauthorized.value -> HttpStatusCode.Unauthorized.description
              HttpStatusCode.Forbidden.value -> HttpStatusCode.Forbidden.description
              HttpStatusCode.NotFound.value -> HttpStatusCode.NotFound.description
              HttpStatusCode.InternalServerError.value -> HttpStatusCode.InternalServerError.description
              else -> code.toString()
            }
            LoadingEvent.Error(message, throwable, code)
          }

          is JsonParseException -> LoadingEvent.Error("Data parsing error.", throwable)
          else -> LoadingEvent.Error("Unexpected error occurred.", throwable)
        }

        /*************************************/
        println(onError)
        /*************************************/
      }
      .onSuccess {
//        appDatabase.paymentDao.insert(it.toPaymentEntity())
      }

    _state.update { it.copy(bitmapImage = null) }
  }

  fun onEvent(event: PaymentEvent) {
    when (event) {
      is PaymentEvent.Button.Pay -> onPay(event)
      is PaymentEvent.Button.ImageBitmap -> _state.update { it.copy(bitmapImage = event.bitmap) }

      is PaymentEvent.Button.NumberOfMonths -> onNumberOfMonths(event)
      else -> TODO("Handle actions")
    }
  }

  private fun onNumberOfMonths(event: PaymentEvent.Button.NumberOfMonths) {
    val minValue = 1
    val maxValue = 9
    val numberOfMonths = (
      if (event.isAdd) {
        state.value.numberOfMonths.plus(1)
      } else {
        state.value.numberOfMonths.minus(1)
      }
      ).coerceIn(minValue, maxValue)
    _state.update { it.copy(numberOfMonths = numberOfMonths) }
  }
}
