package net.techandgraphics.wastical.data.local.database.dashboard.payment

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.RoomWarnings
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.Status
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.dashboard.account.Payment4CurrentMonth
import java.time.YearMonth

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

  @Query(
    """
    SELECT
      account.*,
      plans.fee as amount,
      CASE WHEN payment.id IS NOT NULL THEN 1 ELSE 0 END as hasPaid,
      CASE
        WHEN EXISTS (
          SELECT 1
          FROM payment_month_covered pmc
          JOIN (
            SELECT account_id, SUM(months) AS total_months
            FROM payment_request
            GROUP BY account_id
          ) pr ON pr.account_id = pmc.account_id
          WHERE pmc.account_id = account.id
            AND :month BETWEEN pmc.month AND (pmc.month + pr.total_months)
            AND pmc.year = :year
        ) THEN 1
        ELSE 0
      END AS offlinePay
    FROM
      account AS account
      INNER JOIN account_payment_plan accountplans ON account.id = accountplans.account_id
      INNER JOIN  payment_plan plans ON accountplans.payment_plan_id = plans.id
      LEFT JOIN company_location as location ON account.company_location_id = location.id
      LEFT JOIN demographic_street as district ON location.demographic_district_id = district.id
      LEFT JOIN payment_month_covered as month_covered ON account.id = month_covered.account_id
      AND month_covered.month = :month
      AND month_covered.year = :year
      LEFT JOIN payment as payment ON month_covered.payment_id = payment.id
      WHERE location.demographic_street_id =:id
      AND account.status = 'Active'
      GROUP BY account.id
    ORDER BY offlinePay,
      CASE WHEN :sortOrder = 0 THEN hasPaid END ASC,
      CASE WHEN :sortOrder = 1 THEN hasPaid END DESC,
      CASE WHEN :sortOrder = 2 THEN account.title END ASC,
      CASE WHEN :sortOrder = 3 THEN account.lastname END ASC,
      CASE WHEN :sortOrder = 4 THEN account.username END ASC
""",
  )
  suspend fun getAccountsWithPaymentStatusByStreetId(
    id: Long,
    month: Int,
    year: Int,
    sortOrder: Int = 0,
  ): List<AccountWithPaymentStatusEntity>

  @Query(
    """
    SELECT
      account.*,
      plans.fee as amount,
      CASE WHEN payment.id IS NOT NULL THEN 1 ELSE 0 END as hasPaid,
      CASE
        WHEN EXISTS (
          SELECT 1
          FROM payment_month_covered pmc
          JOIN (
            SELECT account_id, SUM(months) AS total_months
            FROM payment_request
            GROUP BY account_id
          ) pr ON pr.account_id = pmc.account_id
          WHERE pmc.account_id = account.id
            AND :month BETWEEN pmc.month AND (pmc.month + pr.total_months)
            AND pmc.year = :year
        ) THEN 1
        ELSE 0
      END AS offlinePay
  FROM
    account AS account
    INNER JOIN account_payment_plan accountplans ON account.id = accountplans.account_id
    INNER JOIN payment_plan plans ON accountplans.payment_plan_id = plans.id
    LEFT JOIN company_location AS location ON account.company_location_id = location.id
    LEFT JOIN demographic_street AS district ON location.demographic_district_id = district.id
    LEFT JOIN payment_month_covered AS month_covered ON account.id = month_covered.account_id
      AND month_covered.month = :month
      AND month_covered.year = :year
    LEFT JOIN payment AS payment ON month_covered.payment_id = payment.id
    WHERE location.demographic_street_id = :id
      AND account.status = 'Active'
      GROUP BY account.id
    ORDER BY offlinePay,
      CASE WHEN :sortOrder = 0 THEN hasPaid END ASC,
      CASE WHEN :sortOrder = 1 THEN hasPaid END DESC,
      CASE WHEN :sortOrder = 2 THEN account.title END ASC,
      CASE WHEN :sortOrder = 3 THEN account.lastname END ASC,
      CASE WHEN :sortOrder = 4 THEN account.username END ASC
  """,
  )
  fun flowOfAccountsWithPaymentStatusByStreetId(
    id: Long,
    month: Int,
    year: Int,
    sortOrder: Int = 0,
  ): Flow<List<AccountWithPaymentStatusEntity>>

  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query(
    """
    SELECT
      account.title,
      account.firstname,
      account.lastname,
      account.username as contact,
      street.name as demographicStreet,
      area.name as demographicArea,
      plans.fee as amount,
      COALESCE(month_covered.created_at, 0) as paidOn,
      CASE WHEN payment.id IS NOT NULL THEN 1 ELSE 0 END as hasPaid
    FROM
      account AS account
      INNER JOIN account_payment_plan accountplans ON account.id = accountplans.account_id
      INNER JOIN payment_plan plans ON accountplans.payment_plan_id = plans.id
      LEFT JOIN company_location as location ON account.company_location_id = location.id
      LEFT JOIN demographic_street as street ON location.demographic_street_id = street.id
      LEFT JOIN demographic_area as area ON location.demographic_area_id = area.id
      LEFT JOIN payment_month_covered as month_covered ON account.id = month_covered.account_id
      AND (month_covered.year || '-' || printf('%02d', month_covered.month)) IN (:yearMonthKeys)
      LEFT JOIN payment as payment ON month_covered.payment_id = payment.id
      AND payment.payment_status = 'Approved'
    WHERE
      (CASE WHEN payment.id IS NOT NULL THEN 1 ELSE 0 END) = CASE WHEN :hasPaid THEN 1 ELSE 0 END
      AND account.status = :status
      AND (
        strftime('%Y-%m', datetime(account.created_at / 1000, 'unixepoch')) <= :maxYearMonth
      )
      GROUP BY account.id
    ORDER BY street.name, payment.created_at
""",
  )
  suspend fun qRange(
    yearMonthKeys: List<String>,
    hasPaid: Boolean,
    maxYearMonth: String,
    status: String = Status.Active.name,
  ): List<UnPaidAccount>

  @Query(
    """
  SELECT
    a.id AS accountId,
    a.title,
    a.firstname || ' ' || a.lastname AS fullName,
    a.username AS phoneNumber,
    pmc.month AS paidMonth
  FROM account a
  LEFT JOIN payment_month_covered pmc
    ON a.id = pmc.account_id AND pmc.year = :year
""",
  )
  suspend fun getCoverageRaw(year: Int): List<CoverageRaw>

  @Query("SELECT month, year FROM payment_month_covered GROUP BY month, year")
  suspend fun getAllMonthsPayments(): List<MonthYear>

  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query(
    """
    SELECT
      a.*,
      COUNT(DISTINCT pmc.year || '-' || printf('%02d', pmc.month)) AS months,
      ds.name as demographicStreet,
      da.name as demographicArea,
      MAX(pmc.month) AS maxMonth,
      MAX(pmc.year) AS maxYear,
      COUNT(DISTINCT pmc.year || '-' || printf('%02d', pmc.month)) * pp.fee AS overpayment
    FROM
      account a
      INNER JOIN payment p ON a.id = p.account_id AND p.payment_status = 'Approved'
      INNER JOIN payment_month_covered pmc ON p.id = pmc.payment_id
      INNER JOIN account_payment_plan app ON a.id = app.account_id
      INNER JOIN payment_plan pp ON pp.id = app.payment_plan_id
      INNER JOIN company_location cl ON cl.id = a.company_location_id
      INNER JOIN demographic_street ds ON ds.id = cl.demographic_street_id
      INNER JOIN demographic_area da ON da.id = cl.demographic_area_id
    GROUP BY
      a.id
    HAVING
      (maxYear > :year OR (maxYear = :year AND maxMonth > :month))
      AND months > 1
    ORDER BY
      maxYear DESC, maxMonth DESC

  """,
  )
  suspend fun qOverpayment(
    month: Int = YearMonth.now().month.value,
    year: Int = YearMonth.now().year,
  ): List<OverpaymentItem>

  @Query(
    """
    SELECT
      a.*,
      COALESCE(COUNT(DISTINCT pmc.year || '-' || printf('%02d', pmc.month)), 0) AS monthCovered,
      pp.fee AS feePlan,
      COALESCE(COUNT(DISTINCT pmc.year || '-' || printf('%02d', pmc.month)), 0) * pp.fee AS totalPaid,
      COALESCE(MAX(pmc.month), 0) AS maxMonth,
      COALESCE(MAX(pmc.year), 0) AS maxYear,
      street.name as demographicStreet,
      area.name as demographicArea
  FROM account a
  JOIN account_payment_plan app ON app.account_id = a.id
  JOIN payment_plan pp ON pp.id = app.payment_plan_id
  JOIN company_location as location ON a.company_location_id = location.id
  JOIN demographic_street as street ON location.demographic_street_id = street.id
  JOIN demographic_area as area ON location.demographic_area_id = area.id
  LEFT JOIN payment p ON a.id = p.account_id AND p.payment_status = 'Approved'
  LEFT JOIN payment_month_covered pmc ON p.id = pmc.payment_id
  WHERE a.status = :status
  GROUP BY a.id
  ORDER BY monthCovered ASC
  """,
  )
  suspend fun qOutstandingBalance(
    status: String = Status.Active.name,
  ): List<OutstandingBalanceItem>

  @Query(
    """
    SELECT
      pmc.month AS month,
      pmc.year AS year,
      IFNULL(SUM(pp.fee), 0) AS collectedTotal,
      (
        SELECT IFNULL(SUM(pp2.fee), 0)
        FROM account a2
        JOIN account_payment_plan app2 ON app2.account_id = a2.id
        JOIN payment_plan pp2 ON pp2.id = app2.payment_plan_id
          AND strftime('%Y-%m', datetime(a2.created_at, 'unixepoch')) <= printf('%04d-%02d', pmc.year, pmc.month)
      ) AS expectedTotal
    FROM payment_month_covered pmc
    JOIN payment p ON p.id = pmc.payment_id AND p.payment_status = 'Approved'
    JOIN account_payment_plan app ON app.account_id = pmc.account_id
    JOIN payment_plan pp ON pp.id = app.payment_plan_id
    WHERE (pmc.year || '-' || printf('%02d', pmc.month)) IN (:yearMonthKeys)
    GROUP BY pmc.year, pmc.month
    ORDER BY pmc.year, pmc.month
    """,
  )
  suspend fun qRevenueSummary(yearMonthKeys: List<String>): List<RevenueSummaryItem>

  @Query(
    """
    SELECT
      gateway.name AS gatewayName,
      COUNT(DISTINCT p.id) AS payments,
      COUNT(pmc.id) AS monthsCovered,
      IFNULL(SUM(pp.fee), 0) AS totalAmount
    FROM payment p
    JOIN payment_method method ON method.id = p.payment_method_id
    JOIN payment_gateway gateway ON gateway.id = method.payment_gateway_id
    JOIN payment_month_covered pmc ON pmc.payment_id = p.id
    JOIN account_payment_plan app ON app.account_id = p.account_id
    JOIN payment_plan pp ON pp.id = app.payment_plan_id
    WHERE p.payment_status = 'Approved'
      AND (pmc.year || '-' || printf('%02d', pmc.month)) IN (:yearMonthKeys)
    GROUP BY gateway.name
    ORDER BY totalAmount DESC
    """,
  )
  suspend fun qPaymentMethodBreakdown(
    yearMonthKeys: List<String>,
  ): List<PaymentMethodBreakdownItem>

  @Query(
    """
    SELECT
      pp.id AS planId,
      pp.name AS planName,
      pp.fee AS fee,
      COUNT(DISTINCT app.account_id) AS accounts,
      IFNULL(SUM(CASE WHEN pmc.id IS NOT NULL THEN pp.fee ELSE 0 END), 0) AS collectedTotal
    FROM payment_plan pp
    LEFT JOIN account_payment_plan app ON app.payment_plan_id = pp.id
    LEFT JOIN account a ON a.id = app.account_id AND a.status = 'Active'
    LEFT JOIN payment p ON p.account_id = a.id AND p.payment_status = 'Approved'
    LEFT JOIN payment_month_covered pmc ON pmc.payment_id = p.id AND (pmc.year || '-' || printf('%02d', pmc.month)) IN (:yearMonthKeys)
    GROUP BY pp.id, pp.name, pp.fee
    ORDER BY fee ASC
    """,
  )
  suspend fun qPlanPerformance(yearMonthKeys: List<String>): List<PlanPerformanceItem>

  @Query(
    """
    SELECT
      da.id AS areaId,
      da.name AS demographicArea,
      ds.name AS demographicStreet,
      COUNT(DISTINCT a.id) AS totalAccounts,
      IFNULL(SUM(CASE WHEN pmc.id IS NOT NULL THEN pp.fee ELSE 0 END), 0) AS collectedTotal
    FROM demographic_area da
    JOIN company_location cl ON cl.demographic_area_id = da.id
    JOIN demographic_street ds ON cl.demographic_street_id = ds.id
    LEFT JOIN account a ON a.company_location_id = cl.id AND a.status = 'Active'
    LEFT JOIN account_payment_plan app ON app.account_id = a.id
    LEFT JOIN payment_plan pp ON pp.id = app.payment_plan_id
    LEFT JOIN payment p ON p.account_id = a.id AND p.payment_status = 'Approved'
    LEFT JOIN payment_month_covered pmc ON pmc.payment_id = p.id AND (pmc.year || '-' || printf('%02d', pmc.month)) IN (:yearMonthKeys)
    GROUP BY ds.name
    ORDER BY collectedTotal DESC
    """,
  )
  suspend fun qLocationCollection(yearMonthKeys: List<String>): List<LocationCollectionItem>

  @Query(
    """
    SELECT
      gateway.name AS gatewayName,
      SUM(CASE WHEN p.payment_status = 'Approved' THEN 1 ELSE 0 END) AS approvedCount,
      COUNT(*) AS totalCount
    FROM payment p
    JOIN payment_method method ON method.id = p.payment_method_id
    JOIN payment_gateway gateway ON gateway.id = method.payment_gateway_id
    JOIN payment_month_covered pmc ON pmc.payment_id = p.id
    WHERE (pmc.year || '-' || printf('%02d', pmc.month)) IN (:yearMonthKeys)
    GROUP BY gateway.name
    ORDER BY approvedCount DESC
    """,
  )
  suspend fun qGatewaySuccess(yearMonthKeys: List<String>): List<GatewaySuccessItem>

  @Query(
    """
    SELECT
      a.*,
      p.id AS paymentId,
      COUNT(pmc.id) AS monthsCoveredThisPayment,
      MIN(pmc.month) AS minMonth,
      MIN(pmc.year) AS minYear,
      MAX(pmc.month) AS maxMonth,
      MAX(pmc.year) AS maxYear
    FROM payment p
    JOIN account a ON a.id = p.account_id
    JOIN payment_month_covered pmc ON pmc.payment_id = p.id
    WHERE p.payment_status = 'Approved'
      AND (pmc.year || '-' || printf('%02d', pmc.month)) IN (:yearMonthKeys)
    GROUP BY p.id
    HAVING monthsCoveredThisPayment > 1
    ORDER BY maxYear DESC, maxMonth DESC
    """,
  )
  suspend fun qUpfrontPaymentsDetail(
    yearMonthKeys: List<String>,
  ): List<UpfrontPaymentDetailItem>

  @Query(
    """
    SELECT
      a.*,
      COALESCE(COUNT(DISTINCT pmc.year || '-' || printf('%02d', pmc.month)), 0) AS monthCovered,
      pp.fee AS feePlan,
      a.created_at AS createdAt
    FROM account a
    JOIN account_payment_plan app ON app.account_id = a.id
    JOIN payment_plan pp ON pp.id = app.payment_plan_id
    LEFT JOIN payment p ON a.id = p.account_id AND p.payment_status = 'Approved'
    LEFT JOIN payment_month_covered pmc ON p.id = pmc.payment_id
    WHERE a.status = 'Active'
    GROUP BY a.id
    ORDER BY monthCovered ASC
    """,
  )
  suspend fun qAgingRaw(): List<AgingRawItem>

  @Query(
    """
    SELECT
      a.*,
      COALESCE(COUNT(DISTINCT pmc.year || '-' || printf('%02d', pmc.month)), 0) AS monthCovered,
      pp.fee AS feePlan,
      a.created_at AS createdAt
    FROM account a
    JOIN account_payment_plan app ON app.account_id = a.id
    JOIN payment_plan pp ON pp.id = app.payment_plan_id
    LEFT JOIN payment p ON a.id = p.account_id AND p.payment_status = 'Approved'
    LEFT JOIN payment_month_covered pmc ON p.id = pmc.payment_id
    WHERE a.id = :accountId
    GROUP BY a.id
    """,
  )
  suspend fun qAgingRawByAccountId(accountId: Long): AgingRawItem?
}

data class OutstandingBalanceItem(
  @Embedded
  val account: AccountEntity,
  val monthCovered: Int,
  val feePlan: Int,
  val totalPaid: Int,
  val maxMonth: Int,
  val maxYear: Int,
  val demographicStreet: String,
  val demographicArea: String,
)

data class OverpaymentItem(
  @Embedded
  val account: AccountEntity,
  val months: Int,
  val maxMonth: Int,
  val maxYear: Int,
  val overpayment: Int,
  val demographicStreet: String,
  val demographicArea: String,
)

data class CoverageRaw(
  val accountId: Long,
  val fullName: String,
  val title: String,
  val phoneNumber: String,
  val paidMonth: Int?,
)

data class PaymentPlanAgainstAccounts(
  val planId: Long,
  val planName: String,
  val fee: Int,
  val period: String,
  val accountCount: Int,
  val expectedRevenue: Int,
)

data class AccountWithPaymentStatusEntity(
  @Embedded
  val account: AccountEntity,
  val hasPaid: Boolean,
  val offlinePay: Boolean,
  val amount: Int,
)

data class MonthYear(val month: Int, val year: Int)

data class MonthYearPayment4Month(
  val monthYear: MonthYear,
  val payment4CurrentMonth: Payment4CurrentMonth,
)

data class UnPaidAccount(
  val title: String,
  val firstname: String,
  val lastname: String,
  val demographicStreet: String,
  val demographicArea: String,
  val contact: String,
  val amount: Int,
  val paidOn: Long,
)

data class RevenueSummaryItem(
  val month: Int,
  val year: Int,
  val expectedTotal: Int,
  val collectedTotal: Int,
)

data class PaymentMethodBreakdownItem(
  val gatewayName: String,
  val payments: Int,
  val monthsCovered: Int,
  val totalAmount: Int,
)

data class PlanPerformanceItem(
  val planId: Long,
  val planName: String,
  val fee: Int,
  val accounts: Int,
  val collectedTotal: Int,
)

data class LocationCollectionItem(
  val areaId: Long,
  val demographicArea: String,
  val demographicStreet: String,
  val totalAccounts: Int,
  val collectedTotal: Int,
)

data class GatewaySuccessItem(
  val gatewayName: String,
  val approvedCount: Int,
  val totalCount: Int,
)

data class UpfrontPaymentDetailItem(
  @Embedded val account: AccountEntity,
  val paymentId: Long,
  val monthsCoveredThisPayment: Int,
  val minMonth: Int,
  val minYear: Int,
  val maxMonth: Int,
  val maxYear: Int,
)

data class AgingRawItem(
  @Embedded val account: AccountEntity,
  val monthCovered: Int,
  val feePlan: Int,
  val createdAt: Long,
)

enum class AccountSortOrder { Unpaid, Paid, Title, Lastname, Contact }
