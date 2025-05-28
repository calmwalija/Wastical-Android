package net.techandgraphics.wastemanagement.data.remote

sealed class ApiResult<out T> {
  object Loading : ApiResult<Nothing>()
  data class Success<T>(val data: T) : ApiResult<T>()
  data class Error(
    val message: String,
    val cause: Throwable? = null,
    val code: Int? = null,
  ) : ApiResult<Nothing>()
}
