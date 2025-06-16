package net.techandgraphics.wastemanagement.data.local.database.payment.pay

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.local.database.dashboard.street.Payment4CurrentLocationMonth
import net.techandgraphics.wastemanagement.data.local.database.query.PaymentWithAccountAndMethodWithGatewayQuery
import net.techandgraphics.wastemanagement.data.local.database.relations.PaymentWithAccountEntity
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus.Approved

@Dao interface PaymentDao : BaseDao<PaymentEntity> {

  @Query("SELECT * FROM payment WHERE payment_status !=:status ORDER BY id DESC LIMIT 4")
  fun flowOfPayment(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id DESC LIMIT 3")
  fun flowOfInvoice(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id")
  fun flowOfAllInvoices(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT id FROM payment ORDER BY id DESC LIMIT 1")
  fun getLastId(): Flow<Long?>

  @Query("SELECT * FROM payment WHERE payment_status=:status")
  suspend fun qPaymentByStatus(status: String = PaymentStatus.Waiting.name): List<PaymentEntity>

  @Transaction
  @Query("SELECT * FROM payment WHERE payment_status=:status ORDER BY id DESC")
  fun flowOfPaymentAccount(status: String = Approved.name): Flow<List<PaymentWithAccountEntity>>

  @Query("SELECT * FROM payment ORDER BY updated_at DESC LIMIT 1")
  suspend fun getByUpdatedAtLatest(): PaymentEntity?

  @Query("SELECT * FROM payment WHERE account_id=:id")
  fun flowOfByAccountId(id: Long): Flow<List<PaymentEntity>>

  @Query(
    """
        SELECT

        -- Payment
        payment.id AS paymentId,
        payment.screenshot_text AS screenshotText,
        payment.transaction_id AS transactionId,
        payment.payment_method_id AS paymentMethodId,
        payment.account_id AS accountId,
        payment.payment_status AS paymentStatus,
        payment.created_at AS paymentCreatedAt,
        payment.updated_at AS paymentUpdatedAt,
        payment.company_id AS paymentCompanyId,
        payment.executed_by_id AS executedById,

        -- Account
        account.id AS accId,
        account.uuid AS uuid,
        account.title AS title,
        account.firstname AS firstname,
        account.lastname AS lastname,
        account.username AS username,
        account.email AS email,
        account.latitude AS latitude,
        account.longitude AS longitude,
        account.status AS accStatus,
        account.company_location_id AS companyLocationId,
        account.company_id AS accCompanyId,
        account.leaving_reason AS leavingReason,
        account.leaving_timestamp AS leavingTimestamp,
        account.updated_at AS accUpdatedAt,
        account.created_at AS accCreatedAt,

        -- Method
        method.id AS methodId,
        method.account AS methodAccount,
        method.isSelected AS isSelected,
        method.payment_plan_id AS paymentPlanId,
        method.payment_gateway_id AS paymentGatewayId,
        method.created_at AS methodCreatedAt,
        method.updated_at AS methodUpdatedAt,

        -- Gateway
        gateway.id AS gatewayId,
        gateway.name AS gatewayName,
        gateway.type AS gatewayType,
        gateway.created_at AS gatewayCreatedAt,
        gateway.updated_at AS gatewayUpdatedAt,

        -- Plan
        plans.id AS planId,
        plans.fee AS planFee,
        plans.name AS planName,
        plans.period AS planPeriod,
        plans.status AS planStatus,
        plans.company_id AS planCompanyId,
        plans.created_at AS planCreatedAt,
        plans.created_at AS planUpdatedAt

        FROM payment AS payment
        INNER JOIN account AS account ON account.id = payment.account_id
        INNER JOIN payment_method AS method ON method.id = payment.payment_method_id
        INNER JOIN payment_gateway AS gateway ON gateway.id = method.payment_gateway_id
        INNER JOIN payment_plan AS plans ON plans.id = method.payment_plan_id
        WHERE payment.payment_status = :status
    """,
  )
  fun qPaymentWithAccountAndMethodWithGateway(status: String = Approved.name): Flow<List<PaymentWithAccountAndMethodWithGatewayQuery>>

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
    ORDER BY paidAccounts DESC
    """,
  )
  suspend fun qPayment4CurrentLocationMonth(month: Int, year: Int): List<Payment4CurrentLocationMonth>
}
