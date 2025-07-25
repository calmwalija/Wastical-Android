package net.techandgraphics.wastical.domain.model.account

data class AccountContactUiModel(
  val id: Long,
  val uuid: String,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  val accountId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
