package net.techandgraphics.wastical.keycloak

import com.google.gson.JsonObject
import net.techandgraphics.wastical.data.remote.ServerResponse
import net.techandgraphics.wastical.data.remote.account.AccountRequest

interface KeycloakApi {

  suspend fun getToken(keycloakSignInRequest: KeycloakSignInRequest): JsonObject

  suspend fun create(accountRequest: AccountRequest): ServerResponse
}
