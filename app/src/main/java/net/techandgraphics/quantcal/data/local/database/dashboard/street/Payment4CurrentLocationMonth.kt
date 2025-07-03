package net.techandgraphics.quantcal.data.local.database.dashboard.street

data class Payment4CurrentLocationMonth(
  val streetId: Long,
  val streetName: String,
  val areaName: String,
  val totalAccounts: Int,
  val paidAccounts: Int,
  val districtRegion: String,
  val districtName: String,
  val latitude: Float,
  val longitude: Float,
)
