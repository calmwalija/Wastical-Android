package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.JsonObject
import net.techandgraphics.wastemanagement.data.remote.account.AccountRequest
import net.techandgraphics.wastemanagement.data.remote.account.session.AccountSessionResponse

interface KeycloakApi {

  suspend fun getToken(keycloakSignInRequest: KeycloakSignInRequest): JsonObject

  suspend fun create(accountRequest: AccountRequest): AccountSessionResponse
}
