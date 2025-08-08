package net.techandgraphics.wastical.data.local.database.dashboard.account

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.account.ACCOUNT_QUERY_EXPORT
import net.techandgraphics.wastical.data.local.database.account.ReportAccountItem

@Dao interface AccountIndicatorDao {

  @Query(
    """
     SELECT
        COUNT(DISTINCT p.account_id) as totalPaidAccounts,
        SUM(pp.fee) as totalPaidAmount
    FROM payment p
    INNER JOIN payment_month_covered pmc ON p.id = pmc.payment_id
    INNER JOIN payment_method pm ON pm.id = p.payment_method_id
    INNER JOIN payment_plan pp ON pp.id = pm.payment_plan_id
    WHERE pmc.month = :month AND pmc.year = :year

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

  @Query(
    """
    SELECT SUM(pp.fee) FROM payment p
    JOIN payment_method pm ON pm.id = p.payment_method_id
    JOIN payment_plan pp ON pp.id = pm.payment_plan_id
  """,
  )
  suspend fun getTotalAmountReceived(): Int?

  @Query("SELECT created_at FROM account GROUP BY created_at ORDER BY created_at")
  suspend fun qMonthsCreated(): List<Long>

  @Query(
    """
    $ACCOUNT_QUERY_EXPORT
    WHERE a.status = :status
    AND a.role = :role
    ORDER BY ds.name, a.lastname, a.updated_at
  """,
  )
  suspend fun qActiveAccounts(
    status: String = Status.Active.name,
    role: String = AccountRole.Client.name,
  ): List<ReportAccountItem>

  @Query(
    """
    $ACCOUNT_QUERY_EXPORT
    WHERE a.status = :status
    AND ds.id IN (:streets)
    AND da.id IN (:areas)
    AND a.role = :role
    ORDER BY ds.name, a.lastname, createdAt
  """,
  )
  suspend fun qLocationBased(
    areas: List<Long>,
    streets: List<Long>,
    status: String = Status.Active.name,
    role: String = AccountRole.Client.name,
  ): List<ReportAccountItem>

  @Query(
    """
     $ACCOUNT_QUERY_EXPORT
      WHERE a.created_at BETWEEN :start AND :end
    AND a.status = :status
    AND a.role = :role
    ORDER BY ds.name, a.lastname, createdAt
  """,
  )
  suspend fun qRange(
    start: Long,
    end: Long,
    status: String = Status.Active.name,
    role: String = AccountRole.Client.name,
  ): List<ReportAccountItem>

  @Query(
    """
  SELECT
    da.name as theArea,
    cl.id as locationId,
    ds.id as theStreetId,
    da.id as theAreaId,
    ds.name as theStreet
    FROM
    company_location cl
    JOIN demographic_area da ON da.id = cl.demographic_area_id
    JOIN demographic_street ds ON ds.id = cl.demographic_street_id
    ORDER BY ds.name
  """,
  )
  suspend fun qDemographics(): List<DemographicItem>
}

data class DemographicItem(
  val locationId: Long,
  val theAreaId: Long,
  val theStreetId: Long,
  val theArea: String,
  val theStreet: String,
)
