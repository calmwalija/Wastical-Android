package net.techandgraphics.wastemanagement.data.local.database.dashboard.street

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns

@Dao
interface StreetIndicatorDao {

  @Query(
    """
    SELECT S.name AS streetName, COUNT(A.id) AS accountCount
    FROM demographic_street S
    LEFT JOIN account A ON S.id = A.id
    GROUP BY S.id
""",
  )
  suspend fun getAccountCountPerStreet(): List<StreetAccountStat>

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
    GROUP BY ds.id, ds.name, da.name
    ORDER BY paidAccounts DESC LIMIT 3
    """,
  )
  suspend fun getPayment4CurrentLocationMonth(month: Int, year: Int): List<Payment4CurrentLocationMonth>

  @Query(
    """
    SELECT S.name AS streetName, SUM(P.payment_plan_fee) AS totalPayments
    FROM payment P
    INNER JOIN account A ON P.id = A.id
    INNER JOIN demographic_street S ON A.id = S.id
    GROUP BY S.id
""",
  )
  suspend fun getTotalPaymentsPerStreet(): List<StreetPaymentStat>

  @Query(
    """
    SELECT S.name AS streetName, COUNT(A.id) AS unpaidAccounts
    FROM demographic_street S
    INNER JOIN account A ON S.id = A.id
    WHERE A.status = 'active'
    AND A.id NOT IN (
        SELECT account_id FROM payment
        WHERE strftime('%Y-%m', datetime(created_at / 1000, 'unixepoch')) = strftime('%Y-%m', 'now')
        AND status = 'successful'
    )
    GROUP BY S.id
    ORDER BY unpaidAccounts DESC
""",
  )
  suspend fun getUnpaidAccountsPerStreet(): List<StreetUnpaidStat>
}
