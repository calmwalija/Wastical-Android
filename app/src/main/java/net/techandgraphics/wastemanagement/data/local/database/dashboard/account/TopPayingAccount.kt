package net.techandgraphics.wastemanagement.data.local.database.dashboard.account

data class TopPayingAccount(
  val id: Long,
  val firstname: String,
  val lastname: String,
  val totalPaid: Int,
)
