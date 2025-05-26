package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.JsonObject
import net.techandgraphics.wastemanagement.data.remote.account.AccountRequest
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface KeycloakApi {

  @POST("/keycloak/token")
  suspend fun getToken(@Body keycloakSignInRequest: KeycloakSignInRequest): Response<JsonObject>

  @POST("/keycloak/create")
  suspend fun create(@Body signUpAccountRequest: SignUpAccountRequest): Response<Void>

  @POST("keycloak/create")
  suspend fun create(@Body accountRequest: AccountRequest): AccountSessionResponse

  @GET suspend fun get(@Url url: String): List<String>

  @GET("/keycloak/email/{email}")
  suspend fun emailUnique(@Path("email") email: String): Response<JsonObject>
}
