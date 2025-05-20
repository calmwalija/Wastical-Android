package net.techandgraphics.wastemanagement.data.remote

sealed class LoadingEvent<out T> {
  object Loading : LoadingEvent<Nothing>()
  data class Success<T>(val data: T) : LoadingEvent<T>()
  data class Error(
    val message: String,
    val cause: Throwable? = null,
    val code: Int? = null,
  ) : LoadingEvent<Nothing>()
}
