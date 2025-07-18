package net.techandgraphics.qgateway.data.remote

import com.google.gson.annotations.SerializedName
import net.techandgraphics.qgateway.data.local.database.otp.OtpEntity

data class OtpResponse(
  val id: Long,
  val otp: Int,
  val contact: String,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
) {
  fun toOtpEntity() =
    OtpEntity(
      id = id,
      otp = otp,
      contact = contact,
      accountId = accountId,
      createdAt = createdAt,
      updatedAt = updatedAt,
    )
}
