package net.techandgraphics.wastemanagement.data.local.database.dashboard.street

data class StreetPaidThisMonthIndicator(
  val streetName: String,
  val areaName: String,
  val totalAccounts: Int,
  val paidAccounts: Int,
)
