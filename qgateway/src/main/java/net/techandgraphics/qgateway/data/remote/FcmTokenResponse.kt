package net.techandgraphics.qgateway.data.remote

import com.google.gson.annotations.SerializedName
import net.techandgraphics.qgateway.data.local.database.token.FcmTokenEntity

data class FcmTokenResponse(
  val id: Long,
  val token: String,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
) {

  fun toFcmTokenEntity() = FcmTokenEntity(
    token = token,
    sync = false,
    createdAt = createdAt,
    updatedAt = updatedAt,
  )
}
