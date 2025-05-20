package net.techandgraphics.wastemanagement.data.remote.session

import retrofit2.http.GET

interface SessionApi {
  @GET("session")
  suspend fun get(): SessionResponse
}
