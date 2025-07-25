package net.techandgraphics.wastical.domain.model.account

import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.account.AccountTitle

data class AccountUiModel(
  val id: Long,
  val uuid: String,
  val title: AccountTitle,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  val status: Status,
  val companyId: Long,
  val role: String,
  val companyLocationId: Long,
  val leavingReason: String? = null,
  val leavingTimestamp: Long?,
  val updatedAt: Long,
  val latitude: Float = -1f,
  val longitude: Float = -1f,
  val createdAt: Long,
)
