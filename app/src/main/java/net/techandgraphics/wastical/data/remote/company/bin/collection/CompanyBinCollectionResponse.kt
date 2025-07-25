package net.techandgraphics.wastical.data.remote.company.bin.collection

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

data class CompanyBinCollectionResponse(
  val id: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
  @SerializedName("day_of_week") val dayOfWeek: DayOfWeek,
  @SerializedName("company_location_id") val companyLocationId: Long,
)
