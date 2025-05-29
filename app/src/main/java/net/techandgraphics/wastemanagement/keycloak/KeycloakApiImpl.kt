package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import net.techandgraphics.wastemanagement.data.remote.account.AccountRequest
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse
import javax.inject.Inject

class KeycloakApiImpl @Inject constructor(
  private val httpClient: HttpClient,
) : KeycloakApi {

  companion object {
    private const val URL_STRING = "keycloak"
  }

  override suspend fun getToken(keycloakSignInRequest: KeycloakSignInRequest) =
    httpClient.post("$URL_STRING/token") {
      setBody(keycloakSignInRequest)
    }.body<JsonObject>()

  override suspend fun create(accountRequest: AccountRequest) =
    httpClient.post("$URL_STRING/create") {
      setBody(accountRequest)
    }.body<AccountSessionResponse>()
}
