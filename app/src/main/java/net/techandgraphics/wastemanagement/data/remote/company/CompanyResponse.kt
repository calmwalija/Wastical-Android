package net.techandgraphics.wastemanagement.data.remote.company

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.local.database.enums.Status

data class CompanyResponse(
  val id: Long,
  val name: String,
  val latitude: Float,
  val email: String,
  val longitude: Float,
  val status: Status,
  @SerializedName("contact_number") val contactNumber: String,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long?,
)
