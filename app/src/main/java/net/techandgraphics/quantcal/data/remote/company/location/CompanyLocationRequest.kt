package net.techandgraphics.quantcal.data.remote.company.location

import com.google.gson.annotations.SerializedName

data class CompanyLocationRequest(
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("demographic_street_id") val demographicStreetId: Long,
  @SerializedName("demographic_area_id") val demographicAreaId: Long,
  @SerializedName("demographic_district_id") val demographicDistrictId: Long = 12,
)
