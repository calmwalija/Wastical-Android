package net.techandgraphics.wastical.data.local.database.dashboard.account

data class PaidThisMonthIndicator(
  val totalAccounts: Int,
  val totalPaid: Int,
  val accountsPaidThisMonth: Int,
  val percentPaid: Float,
)
