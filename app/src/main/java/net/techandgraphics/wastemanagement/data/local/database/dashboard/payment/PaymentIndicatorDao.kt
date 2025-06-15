package net.techandgraphics.wastemanagement.data.local.database.dashboard.payment

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PaymentIndicatorDao {

  @Query(
    """
      SELECT
        pp.id AS planId,
        pp.name AS planName,
        pp.fee AS fee,
        pp.period AS period,
        COUNT(app.account_id) AS accountCount,
        (pp.fee * COUNT(app.account_id)) AS expectedRevenue
      FROM payment_plan pp
      LEFT JOIN account_payment_plan app ON pp.id = app.payment_plan_id
      GROUP BY pp.id
    """,
  )
  suspend fun getPaymentPlanAgainstAccounts(): List<PaymentPlanAgainstAccounts>

  @Query(
    """
    SELECT SUM(pp.fee) AS expectedTotal
    FROM account_payment_plan app
    INNER JOIN payment_plan pp ON app.payment_plan_id = pp.id
""",
  )
  suspend fun getExpectedAmountToCollect(): Int
}

data class PaymentPlanAgainstAccounts(
  val planId: Long,
  val planName: String,
  val fee: Int,
  val period: String,
  val accountCount: Int,
  val expectedRevenue: Int,
)
