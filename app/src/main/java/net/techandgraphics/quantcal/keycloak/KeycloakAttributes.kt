package net.techandgraphics.quantcal.keycloak

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class KeycloakAttributes(
  @SerializedName("company_id") val companyId: List<String>,
  @SerializedName("contact_number") val contactNumber: List<String>,
  @SerializedName("title") val title: List<String>?,
)
