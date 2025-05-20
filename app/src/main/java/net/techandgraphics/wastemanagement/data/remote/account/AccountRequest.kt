package net.techandgraphics.wastemanagement.data.remote.account

import java.util.UUID

data class AccountRequest(
  val uuid: String = UUID.randomUUID().toString(),
  val title: String,
  val firstname: String?,
  val lastname: String,
  val companyId: Long,
  val contacts: List<String>,
  val email: String,
)
