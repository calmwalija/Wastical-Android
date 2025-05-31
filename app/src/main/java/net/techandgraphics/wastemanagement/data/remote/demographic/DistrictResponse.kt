package net.techandgraphics.wastemanagement.data.remote.demographic

import com.google.gson.annotations.SerializedName

data class DistrictResponse(
  val id: Long,
  val name: String,
  val region: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
