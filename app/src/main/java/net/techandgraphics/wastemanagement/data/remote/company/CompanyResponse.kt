package net.techandgraphics.wastemanagement.data.remote.company

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.Status

data class CompanyResponse(
  val id: Long,
  val name: String,
  val latitude: Float,
  val email: String,
  val longitude: Float,
  val status: Status,
  val address: String,
  val slogan: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
