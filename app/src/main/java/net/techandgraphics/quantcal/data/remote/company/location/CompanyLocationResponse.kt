package net.techandgraphics.quantcal.data.remote.company.location

import com.google.gson.annotations.SerializedName
import net.techandgraphics.quantcal.data.Status

data class CompanyLocationResponse(
  val id: Long,
  val status: Status,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("demographic_street_id") val demographicStreetId: Long,
  @SerializedName("demographic_area_id") val demographicAreaId: Long,
  @SerializedName("demographic_district_id") val demographicDistrictId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
