package net.techandgraphics.wastemanagement.data.local.database.dashboard.account

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import java.time.ZoneId
import java.time.ZonedDateTime

@Dao interface AccountIndicatorDao {

  @Query(
    """
     SELECT
        COUNT(DISTINCT p.account_id) as totalPaidAccounts,
        SUM(payment_plan_fee) as totalPaidAmount
    FROM payment p
    INNER JOIN payment_month_covered pm ON p.id = pm.payment_id
    WHERE pm.month = :month AND pm.year = :year

""",
  )
  suspend fun getPayment4CurrentMonth(month: Int, year: Int): Payment4CurrentMonth

  @Query(
    """
        SELECT IFNULL(SUM(pp.fee), 0)
        FROM account a
        JOIN payment_plan pp ON a.company_id = pp.company_id
    """,
  )
  suspend fun getExpectedTotalThisMonth(): Int

  @Query(
    """
    SELECT SUM(payment_plan_fee)
    FROM payment
    WHERE strftime('%Y-%m', datetime(created_at / 1000, 'unixepoch')) = strftime('%Y-%m', 'now')
""",
  )
  suspend fun getTotalPaymentsThisMonth(): Int?

  @Query(
    """
    SELECT COUNT(*)
    FROM account
    WHERE id NOT IN (
        SELECT DISTINCT id
        FROM payment
        WHERE strftime('%Y-%m', datetime(created_at / 1000, 'unixepoch')) = strftime('%Y-%m', 'now')
    )
""",
  )
  suspend fun getTotalUnpaidAccountsThisMonth(): Int

  @Query("SELECT SUM(payment_plan_fee) FROM payment")
  suspend fun getTotalAmountReceived(): Int?

  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query(
    """
    SELECT A.*, SUM(P.payment_plan_fee) as totalPaid
    FROM account A
    JOIN payment P ON A.id = P.id
    GROUP BY A.id
    ORDER BY totalPaid DESC
    LIMIT 10
""",
  )
  suspend fun getTopPayingAccounts(): List<TopPayingAccount>

//  @Query(
//    """
//    SELECT s.name AS streetName, COUNT(a.id) AS accountCount
//    FROM account a
//    INNER JOIN demographic_street s ON a.street_id = s.id
//    GROUP BY s.id
// """,
//  )
//  fun getAccountsPerStreet(): List<StreetAccountCount>
}

fun getMonthStartTimestamp(year: Int, month: Int): Long {
  val zone = ZoneId.systemDefault()
  val startOfMonth = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, zone)
  return (startOfMonth.toEpochSecond()).also { println(it) }
}
