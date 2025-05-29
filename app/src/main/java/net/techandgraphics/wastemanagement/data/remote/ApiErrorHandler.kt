package net.techandgraphics.wastemanagement.data.remote

import com.google.gson.JsonParseException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

fun onApiErrorHandler(throwable: Throwable) = when (throwable) {
  is IOException -> ApiResult.Error("Please check your connection.", throwable)

  is HttpException -> {
    val code = throwable.code()
    val message = when (code) {
      HttpStatusCode.fromValue(code).value -> HttpStatusCode.fromValue(code).description
      else -> code.toString()
    }
    ApiResult.Error(message, throwable, code)
  }

  is JsonParseException -> ApiResult.Error("Data parsing error.", throwable)
  else -> ApiResult.Error("Unexpected error occurred.", throwable)
}

fun mapApiError(throwable: Throwable): ApiResult.Error = when (throwable) {
  is IOException -> ApiResult.Error("Please check your connection.", throwable)
  is ClientRequestException -> {
    val code = throwable.response.status.value
    val message = throwable.response.status.description
    ApiResult.Error(message, throwable, code)
  }

  is ServerResponseException -> {
    val code = throwable.response.status.value
    val message = throwable.response.status.description
    ApiResult.Error(message, throwable, code)
  }

  is SerializationException -> ApiResult.Error("Data parsing error.", throwable)
  else -> ApiResult.Error("Unexpected error occurred.", throwable)
}
