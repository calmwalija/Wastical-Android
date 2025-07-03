package net.techandgraphics.quantcal.domain.model.account

data class AccountFcmTokenUiModel(
  val id: Long,
  val token: String,
  val sync: Boolean = true,
  val accountId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
