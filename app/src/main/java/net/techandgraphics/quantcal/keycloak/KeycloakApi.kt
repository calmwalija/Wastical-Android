package net.techandgraphics.quantcal.keycloak

import com.google.gson.JsonObject
import net.techandgraphics.quantcal.data.remote.ServerResponse
import net.techandgraphics.quantcal.data.remote.account.AccountRequest

interface KeycloakApi {

  suspend fun getToken(keycloakSignInRequest: KeycloakSignInRequest): JsonObject

  suspend fun create(accountRequest: AccountRequest): ServerResponse
}
