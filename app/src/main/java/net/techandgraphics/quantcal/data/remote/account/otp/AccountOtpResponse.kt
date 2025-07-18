package net.techandgraphics.quantcal.data.remote.account.otp

import com.google.gson.annotations.SerializedName

data class AccountOtpResponse(
  val id: Long,
  val otp: Int,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
