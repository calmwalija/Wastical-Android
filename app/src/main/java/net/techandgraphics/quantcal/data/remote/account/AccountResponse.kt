package net.techandgraphics.quantcal.data.remote.account

import com.google.gson.annotations.SerializedName

data class AccountResponse(
  val id: Long,
  val uuid: String,
  val title: String,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  val latitude: Float,
  val longitude: Float,
  val status: String,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("leaving_reason") val leavingReason: String?,
  @SerializedName("leaving_timestamp") val leavingTimestamp: Long?,
  @SerializedName("company_location_id") val companyLocationId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
