package net.techandgraphics.wastical.data.remote.account.contact

import com.google.gson.annotations.SerializedName

data class AccountContactResponse(
  val id: Long,
  val uuid: String,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
