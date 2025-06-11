package net.techandgraphics.wastemanagement.data.local.database.account

data class AccountWithStreetAndAreaEntity(
  val lastname: String,
  val username: String,
  val title: String,
  val firstname: String,
  val accountId: Long,
  val streetName: String,
  val areaName: String,
)
