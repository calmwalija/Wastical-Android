package net.techandgraphics.wastemanagement.data.local.database.dashboard.payment

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import net.techandgraphics.wastemanagement.data.local.database.dashboard.account.Payment4CurrentMonth
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.Payment4CurrentLocationMonth

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

  @Query(
    """
    SELECT SUM(pp.fee) AS expectedTotal
    FROM account_payment_plan app
    INNER JOIN payment_plan pp ON app.payment_plan_id = pp.id
    INNER JOIN account acc ON acc.id = app.account_id
    INNER JOIN company_location cl ON cl.id = acc.company_location_id
    WHERE cl.demographic_street_id =:id
""",
  )
  suspend fun getExpectedAmountToCollectByStreetId(id: Long): Int

  @RewriteQueriesToDropUnusedColumns
  @Query(
    """
    SELECT
        ds.id AS streetId,
        ds.name AS streetName,
        da.name AS areaName,
        COUNT(DISTINCT a.id) AS totalAccounts,
        COUNT(DISTINCT p.account_id) AS paidAccounts
    FROM company_location cl
    JOIN account a ON cl.id = a.company_location_id
    JOIN demographic_street ds ON ds.id = cl.demographic_street_id
    JOIN demographic_area da ON da.id = cl.demographic_area_id
    LEFT JOIN (
        SELECT DISTINCT p.account_id
        FROM payment p JOIN payment_month_covered pm ON pm.payment_id = p.id
        WHERE pm.month = :month AND pm.year =:year
    ) p ON p.account_id = a.id
    WHERE ds.id = :id
    GROUP BY ds.id, ds.name, da.name
    ORDER BY paidAccounts DESC LIMIT 3
    """,
  )
  suspend fun getPayment4CurrentLocationMonthById(id: Long, month: Int, year: Int): Payment4CurrentLocationMonth

  @Query(
    """
     SELECT
        COUNT(DISTINCT p.account_id) as totalPaidAccounts,
        SUM(pp.fee) as totalPaidAmount
    FROM payment p
    INNER JOIN payment_month_covered pmc ON p.id = pmc.payment_id
    INNER JOIN payment_method pm ON pm.id = p.payment_method_id
    INNER JOIN payment_plan pp ON pp.id = pm.payment_plan_id
    INNER JOIN account acc ON acc.id = p.account_id
    INNER JOIN company_location cl ON cl.id = acc.company_location_id
    WHERE pmc.month = :month AND pmc.year = :year AND cl.demographic_street_id=:id

""",
  )
  suspend fun getPayment4CurrentMonthByStreetId(
    id: Long,
    month: Int,
    year: Int,
  ): Payment4CurrentMonth
}

data class PaymentPlanAgainstAccounts(
  val planId: Long,
  val planName: String,
  val fee: Int,
  val period: String,
  val accountCount: Int,
  val expectedRevenue: Int,
)
