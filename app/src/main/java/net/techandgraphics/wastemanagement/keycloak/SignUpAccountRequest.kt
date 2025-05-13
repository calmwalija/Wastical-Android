package net.techandgraphics.wastemanagement.keycloak

import com.google.gson.annotations.SerializedName

data class SignUpAccountRequest(
  val password: String,
  val email: String,
  val username: String,
  @SerializedName("full_name") val fullName: String,
)
