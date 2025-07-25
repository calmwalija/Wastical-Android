package net.techandgraphics.wastical.data.remote.company

import com.google.gson.annotations.SerializedName

data class CompanyContactResponse(
  val id: Long,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
