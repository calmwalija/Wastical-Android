package net.techandgraphics.qgateway.data.remote

import com.google.gson.annotations.SerializedName

data class FcmTokenRequest(
  val token: String,
  @SerializedName("account_id") val accountId: Long,
)
