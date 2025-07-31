package net.techandgraphics.wastical.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface LastUpdatedApi {

  @POST("last_updated/{id}")
  suspend fun since(
    @Path("id") id: Long,
    @Body lastUpdated: LastUpdatedMetadata,
  ): Response<ServerResponse>
}
