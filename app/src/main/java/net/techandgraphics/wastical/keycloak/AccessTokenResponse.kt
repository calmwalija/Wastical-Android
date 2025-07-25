package net.techandgraphics.wastical.keycloak

import com.google.gson.annotations.SerializedName

data class AccessTokenResponse(
  @SerializedName("access_token") val accessToken: String,
  @SerializedName("expires_in") val expiresIn: Int,
  @SerializedName("not-before-policy") val notBeforePolicy: Int,
  @SerializedName("refresh_expires_in") val refreshExpiresIn: Int,
  @SerializedName("refresh_token") val refreshToken: String,
  @SerializedName("scope") val scope: String,
  @SerializedName("session_state") val sessionState: String,
  @SerializedName("token_type") val tokenType: String,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("contact_number") val contactNumber: String,
  @SerializedName("title") val title: String,
)
