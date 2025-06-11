package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.JsonObject
import net.techandgraphics.wastemanagement.data.remote.ServerResponse
import net.techandgraphics.wastemanagement.data.remote.account.AccountRequest

interface KeycloakApi {

  suspend fun getToken(keycloakSignInRequest: KeycloakSignInRequest): JsonObject

  suspend fun create(accountRequest: AccountRequest): ServerResponse
}
