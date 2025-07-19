package net.techandgraphics.qgateway.data.remote

import com.google.gson.annotations.SerializedName

data class ServerResponse(
  @SerializedName("account") val accounts: List<AccountResponse>? = null,
  @SerializedName("account_otp") val otps: List<OtpResponse>? = null,
)
