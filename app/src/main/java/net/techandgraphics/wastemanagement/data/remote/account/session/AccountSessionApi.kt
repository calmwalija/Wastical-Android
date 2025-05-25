package net.techandgraphics.wastemanagement.data.remote.account.session

import retrofit2.http.GET

interface AccountSessionApi {
  @GET("session")
  suspend fun get(): AccountSessionResponse
}
