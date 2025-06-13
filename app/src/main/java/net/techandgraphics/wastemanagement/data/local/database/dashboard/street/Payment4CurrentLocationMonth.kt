package net.techandgraphics.wastemanagement.data.local.database.dashboard.street

data class Payment4CurrentLocationMonth(
  val streetName: String,
  val areaName: String,
  val totalAccounts: Int,
  val paidAccounts: Int,
)
