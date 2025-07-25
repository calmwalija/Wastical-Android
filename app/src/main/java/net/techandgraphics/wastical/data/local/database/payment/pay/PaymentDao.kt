package net.techandgraphics.wastical.data.local.database.payment.pay

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastical.data.local.database.query.PaymentWithAccountAndMethodWithGatewayQuery
import net.techandgraphics.wastical.data.local.database.relations.PaymentWithAccountEntity
import net.techandgraphics.wastical.data.local.database.relations.PaymentWithMonthsCoveredEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus.Approved

@Dao interface PaymentDao : BaseDao<PaymentEntity> {

  @Query("SELECT * FROM payment WHERE id=:id")
  suspend fun get(id: Long): PaymentEntity

  @Transaction
  @Query("SELECT * FROM payment WHERE payment_status !=:status ORDER BY id DESC LIMIT 4")
  fun flowOfPaymentsWithMonthCovered(status: String = Approved.name): Flow<List<PaymentWithMonthsCoveredEntity>>

  @Transaction
  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id DESC LIMIT 4")
  fun flowOfInvoicesWithMonthCovered(status: String = Approved.name): Flow<List<PaymentWithMonthsCoveredEntity>>

  @Query("SELECT * FROM payment WHERE payment_status !=:status ORDER BY id DESC LIMIT 4")
  fun flowOfPayment(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id DESC LIMIT 3")
  fun flowOfInvoice(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Transaction
  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id")
  fun flowOfAllInvoices(status: String = Approved.name): Flow<List<PaymentWithMonthsCoveredEntity>>

  @Query("SELECT id FROM payment ORDER BY id DESC LIMIT 1")
  fun getLastId(): Flow<Long?>

  @Query("SELECT * FROM payment WHERE account_id=:id AND created_at=:at")
  suspend fun getByCreatedAt(id: Long, at: Long): PaymentEntity?

  @Query("SELECT * FROM payment WHERE payment_status=:status")
  suspend fun qPaymentByStatus(status: String = PaymentStatus.Waiting.name): List<PaymentEntity>

  @Transaction
  @Query("SELECT * FROM payment WHERE payment_status=:status ORDER BY id DESC")
  fun flowOfPaymentAccount(status: String = Approved.name): Flow<List<PaymentWithAccountEntity>>

  @Query("SELECT * FROM payment ORDER BY updated_at DESC LIMIT 1")
  suspend fun getByUpdatedAtLatest(): PaymentEntity?

  @Query("SELECT * FROM payment WHERE account_id=:id")
  fun flowOfByAccountId(id: Long): Flow<List<PaymentEntity>>

  @Transaction
  @Query("SELECT * FROM payment WHERE account_id=:id")
  fun flowOfWithMonthCoveredByAccountId(id: Long): Flow<List<PaymentWithMonthsCoveredEntity>>

  @Query(
    """
    $PAYMENT_QUERY_BASE
    WHERE payment.payment_status = :status AND account.status = 'Active'
    ORDER BY payment.created_at DESC
  """,
  )
  fun qPaymentWithAccountAndMethodWithGateway(status: String = Approved.name): Flow<List<PaymentWithAccountAndMethodWithGatewayQuery>>

  @Query(
    """
    $PAYMENT_QUERY_BASE
    WHERE payment.payment_status != :status AND account.status = 'Active'
    ORDER BY payment.created_at DESC
  """,
  )
  fun qPaymentWithAccountAndMethodWithGatewayNot(status: String): Flow<List<PaymentWithAccountAndMethodWithGatewayQuery>>

  @Query(
    """
    $PAYMENT_QUERY_BASE
    WHERE payment.payment_status != :status AND account.status = 'Active'
    ORDER BY payment.created_at DESC
    LIMIT :limit
  """,
  )
  fun qPaymentWithAccountAndMethodWithGatewayNotWithLimit(
    status: String,
    limit: Int,
  ): Flow<List<PaymentWithAccountAndMethodWithGatewayQuery>>

  @Query(
    """
    $PAYMENT_QUERY_BASE
    WHERE payment.payment_status = :paymentStatus AND account.status = 'Active'
    ORDER BY payment.created_at DESC LIMIT :limit
  """,
  )
  fun qPaymentWithAccountAndMethodWithGatewayLimit(
    paymentStatus: String = Approved.name,
    limit: Int = 10,
  ): Flow<List<PaymentWithAccountAndMethodWithGatewayQuery>>

  @RewriteQueriesToDropUnusedColumns
  @Query(
    """
    SELECT
        street.id AS streetId,
        street.name AS streetName,
        area.name AS areaName,
        street.name AS streetName,
        district.name AS districtName,
        district.region AS districtRegion,
        street.latitude AS latitude,
        street.longitude AS longitude,
        COUNT(DISTINCT a.id) AS totalAccounts,
        COUNT(DISTINCT p.account_id) AS paidAccounts
    FROM company_location cl
    JOIN account a ON cl.id = a.company_location_id
    JOIN demographic_street street ON street.id = cl.demographic_street_id
    JOIN demographic_area area ON area.id = cl.demographic_area_id
    JOIN demographic_district district ON district.id = cl.demographic_district_id
    LEFT JOIN (
        SELECT DISTINCT p.account_id
        FROM payment p JOIN payment_month_covered pm ON pm.payment_id = p.id
        WHERE pm.month = :month AND pm.year =:year
    ) p ON p.account_id = a.id
        WHERE (street.name LIKE '%' || :query || '%'
        OR area.name LIKE '%' || :query || '%')
        AND a.status = 'Active'
    GROUP BY street.id, street.name, area.name
    ORDER BY streetName ASC
    """,
  )
  fun qPayment4CurrentLocationMonth(
    month: Int,
    year: Int,
    query: String = "",
  ): Flow<List<Payment4CurrentLocationMonth>>

  @Query(
    """
    SELECT
      pp.fee, app.account_id as accountId
    FROM
      payment_month_covered pmc
      JOIN account_payment_plan app ON app.account_id = pmc.account_id
      JOIN payment_plan pp ON pp.id = app.payment_plan_id
    WHERE
      pmc.payment_id IN (
        SELECT
          payment_id
        FROM
          payment_month_covered
        WHERE
          payment_id > 2
        GROUP BY
          payment_id
        HAVING
          COUNT(*) > 1
      )
      AND pmc.month = :month
      AND pmc.year = :year
    ORDER BY
      payment_id,
      pmc.id
  """,
  )
  fun qUpfrontPayments(month: Int, year: Int): List<UpfrontPayment>

  data class UpfrontPayment(val fee: Int, val accountId: Long)
}
