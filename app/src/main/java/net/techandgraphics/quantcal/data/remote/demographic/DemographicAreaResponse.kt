package net.techandgraphics.quantcal.data.remote.demographic

import com.google.gson.annotations.SerializedName

data class DemographicAreaResponse(
  val id: Long,
  val name: String,
  val type: String,
  val latitude: Float,
  val longitude: Float,
  val description: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
