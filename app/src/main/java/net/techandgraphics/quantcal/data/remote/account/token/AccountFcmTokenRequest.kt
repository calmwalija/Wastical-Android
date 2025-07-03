package net.techandgraphics.quantcal.data.remote.account.token

import com.google.gson.annotations.SerializedName

data class AccountFcmTokenRequest(
  val token: String,
  @SerializedName("account_id") val accountId: Long,
)
