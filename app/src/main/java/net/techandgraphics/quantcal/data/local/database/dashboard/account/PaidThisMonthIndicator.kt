package net.techandgraphics.quantcal.data.local.database.dashboard.account

data class PaidThisMonthIndicator(
  val totalAccounts: Int,
  val totalPaid: Int,
  val accountsPaidThisMonth: Int,
  val percentPaid: Float,
)
