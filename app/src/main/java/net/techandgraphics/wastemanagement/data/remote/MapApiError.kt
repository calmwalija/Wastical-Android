package net.techandgraphics.wastemanagement.data.remote

import com.google.gson.JsonParseException
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

fun mapApiError(throwable: Throwable): ApiResult.Error {
  return when (throwable) {
    is IOException -> ApiResult.Error("Please check your internet connection.", throwable)

    is HttpException -> {
      val code = throwable.code()
      val message = when (code) {
        HttpURLConnection.HTTP_BAD_REQUEST -> "Bad Request"
        HttpURLConnection.HTTP_UNAUTHORIZED -> "Unauthorized"
        HttpURLConnection.HTTP_FORBIDDEN -> "Forbidden"
        HttpURLConnection.HTTP_NOT_FOUND -> "Not Found"
        HttpURLConnection.HTTP_INTERNAL_ERROR -> "Internal Server Error"
        HttpURLConnection.HTTP_UNAVAILABLE -> "Service Unavailable"
        else -> "HTTP $code: ${throwable.message() ?: "Unknown HTTP error"}"
      }
      ApiResult.Error(message, throwable, code)
    }

    is JsonParseException -> ApiResult.Error("Failed to parse server response.", throwable)

    else -> ApiResult.Error("Unexpected error occurred.", throwable)
  }
}
