package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.annotations.SerializedName

data class JwtAccount(
  @SerializedName("email") val email: String,
  @SerializedName("email_verified") val emailVerified: Boolean,
  @SerializedName("family_name") val familyName: String,
  @SerializedName("given_name") val givenName: String,
  @SerializedName("name") val name: String,
  @SerializedName("preferred_username") val username: String,
  @SerializedName("scope") val scope: String,
  @SerializedName("sub") val uuid: String,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("contact_number") val contactNumber: String,
  @SerializedName("title") val title: String,
)
