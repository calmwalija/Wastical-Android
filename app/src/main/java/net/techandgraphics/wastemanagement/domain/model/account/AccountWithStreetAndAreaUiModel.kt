package net.techandgraphics.wastemanagement.domain.model.account

data class AccountWithStreetAndAreaUiModel(
  val lastname: String,
  val firstname: String,
  val title: String,
  val username: String,
  val accountId: Long,
  val streetName: String,
  val areaName: String,
)
