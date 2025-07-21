package net.techandgraphics.quantcal.data.local.database.dashboard.payment

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import net.techandgraphics.quantcal.data.local.database.account.AccountEntity
import net.techandgraphics.quantcal.data.local.database.dashboard.account.Payment4CurrentMonth

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
      CASE WHEN payment.id IS NOT NULL THEN 1 ELSE 0 END as hasPaid
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
    ORDER BY
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
      account.title,
      account.firstname,
      account.lastname,
      account.username as contact,
      street.name as demographicStreet,
      plans.fee as amount,
      CASE WHEN payment.id IS NOT NULL THEN 1 ELSE 0 END as hasPaid
    FROM
      account AS account
      INNER JOIN account_payment_plan accountplans ON account.id = accountplans.account_id
      INNER JOIN payment_plan plans ON accountplans.payment_plan_id = plans.id
      LEFT JOIN company_location as location ON account.company_location_id = location.id
      LEFT JOIN demographic_street as street ON location.demographic_street_id = street.id
      LEFT JOIN payment_month_covered as month_covered ON account.id = month_covered.account_id
      AND month_covered.month = :month
      AND month_covered.year = :year
      LEFT JOIN payment as payment ON month_covered.payment_id = payment.id
    WHERE
      hasPaid = :hasPaid
    GROUP BY
      account.id
""",
  )
  suspend fun qAccounts(month: Int, year: Int, hasPaid: Boolean): List<UnPaidAccount>

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
}

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
  val contact: String,
  val amount: Int,
)

enum class AccountSortOrder { Unpaid, Paid, Title, Lastname, Contact }
