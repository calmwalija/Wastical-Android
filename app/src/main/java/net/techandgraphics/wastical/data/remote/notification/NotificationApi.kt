package net.techandgraphics.wastical.data.remote.notification

import net.techandgraphics.wastical.data.remote.ServerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationApi {

  companion object {
    private const val BASE_URL = "notification"
  }

  @GET("$BASE_URL/latest")
  suspend fun latest(
    @Query("id") id: Long,
    @Query("mills") mills: Long,
  ): List<NotificationResponse>

  @POST(BASE_URL)
  suspend fun post(@Body request: NotificationRequest): ServerResponse
}
