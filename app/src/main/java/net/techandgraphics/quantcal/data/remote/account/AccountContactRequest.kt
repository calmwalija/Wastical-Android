package net.techandgraphics.quantcal.data.remote.account

import com.google.gson.annotations.SerializedName

data class AccountContactRequest(
  val uuid: String,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  @SerializedName("account_id") val accountId: Long,
)
