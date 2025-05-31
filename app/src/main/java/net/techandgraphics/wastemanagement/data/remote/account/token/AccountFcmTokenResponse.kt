package net.techandgraphics.wastemanagement.data.remote.account.token

import com.google.gson.annotations.SerializedName

data class AccountFcmTokenResponse(
  val id: Long,
  val token: String,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
