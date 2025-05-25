package net.techandgraphics.wastemanagement.data.remote.company.trash.collection.schedule

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

data class TrashCollectionScheduleResponse(
  val id: Long,
  @SerializedName("day_of_week") val dayOfWeek: DayOfWeek,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("street_id") val streetId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long?,
)
