package net.techandgraphics.qgateway.domain.model

import net.techandgraphics.qgateway.data.local.database.account.AccountEntity

data class AccountUiModel(
  val id: Long,
  val uuid: String,
  val title: String,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  val updatedAt: Long,
  val createdAt: Long,
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
