package net.techandgraphics.wastical.data.remote.company

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastical.data.Status

data class CompanyResponse(
  val id: Long,
  val uuid: String,
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
