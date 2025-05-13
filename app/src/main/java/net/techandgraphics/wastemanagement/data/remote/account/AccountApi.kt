package net.techandgraphics.wastemanagement.data.remote.account

import retrofit2.http.GET
import retrofit2.http.Url

interface AccountApi {

  @GET suspend fun get(@Url url: String)
}
