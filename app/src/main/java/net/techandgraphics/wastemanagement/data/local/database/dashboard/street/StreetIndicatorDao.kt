package net.techandgraphics.wastemanagement.data.local.database.dashboard.street

import androidx.room.Dao
import androidx.room.Query

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

  @Query(
    """
      SELECT
        COUNT(DISTINCT a.id) AS totalAccounts,
        s.name AS streetName,
        da.name AS areaName,
        COUNT(p.account_id) AS paidAccounts
      FROM demographic_street s
      JOIN account a ON s.id = a.street_id
      LEFT JOIN payment p ON p.account_id = a.id
      JOIN demographic_area da ON da.id = s.area_id
      GROUP BY s.id
      ORDER BY paidAccounts DESC
    """,
  )
  suspend fun getStreetPaidThisMonth(): List<StreetPaidThisMonthIndicator>

  @Query(
    """
    SELECT S.name AS streetName, SUM(P.payment_plan_fee * P.number_of_months) AS totalPayments
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
