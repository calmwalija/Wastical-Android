package net.techandgraphics.wastemanagement.data.remote.company

import retrofit2.http.GET
import retrofit2.http.Url

interface CompanyApi {

  @GET suspend fun get(@Url url: String): List<CompanyResponse>
}
