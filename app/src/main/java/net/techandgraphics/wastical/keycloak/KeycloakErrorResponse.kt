package net.techandgraphics.wastical.keycloak

import com.google.gson.annotations.SerializedName

data class KeycloakErrorResponse(
  val error: String,
  @SerializedName("error_description") val description: String,
)
