package net.techandgraphics.wastemanagement.data.remote

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.serialization.SerializationException
import java.io.IOException

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
