package net.techandgraphics.wcompanion.data.remote

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wcompanion.data.local.database.account.AccountEntity

data class AccountResponse(
  val id: Long,
  val uuid: String,
  val title: String,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  @SerializedName("updated_at") val updatedAt: Long,
  @SerializedName("created_at") val createdAt: Long,
) {
  fun toAccountEntity() = AccountEntity(
    id = id,
    uuid = uuid,
    title = title,
    firstname = firstname,
    lastname = lastname,
    username = username,
    email = email,
    updatedAt = updatedAt,
    createdAt = createdAt,
  )
}
