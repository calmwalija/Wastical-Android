package net.techandgraphics.wastemanagement.data.remote

import com.google.gson.JsonParseException
import io.ktor.http.HttpStatusCode
import retrofit2.HttpException
import java.io.IOException

fun onApiErrorHandler(throwable: Throwable) = when (throwable) {
  is IOException -> LoadingEvent.Error("Please check your connection.", throwable)

  is HttpException -> {
    val code = throwable.code()
    val message = when (code) {
      HttpStatusCode.fromValue(code).value -> HttpStatusCode.fromValue(code).description
      else -> code.toString()
    }
    LoadingEvent.Error(message, throwable, code)
  }

  is JsonParseException -> LoadingEvent.Error("Data parsing error.", throwable)
  else -> LoadingEvent.Error("Unexpected error occurred.", throwable)
}
