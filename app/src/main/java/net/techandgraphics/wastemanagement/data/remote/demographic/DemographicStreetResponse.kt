package net.techandgraphics.wastemanagement.data.remote.demographic

import com.google.gson.annotations.SerializedName

data class DemographicStreetResponse(
  val id: Long,
  val name: String,
  val latitude: Float,
  val longitude: Float,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
