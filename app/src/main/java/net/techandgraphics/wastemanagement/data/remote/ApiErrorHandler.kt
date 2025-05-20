package net.techandgraphics.wastemanagement.data.remote

import com.google.gson.JsonParseException
import io.ktor.http.HttpStatusCode
import retrofit2.HttpException
import java.io.IOException

fun onApiErrorHandler(throwable: Throwable) = when (throwable) {
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
