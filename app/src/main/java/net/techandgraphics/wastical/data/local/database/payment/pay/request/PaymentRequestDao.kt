package net.techandgraphics.wastical.data.local.database.payment.pay.request

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.relations.PaymentRequestWithAccountEntity
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus

@Dao interface PaymentRequestDao : BaseDao<PaymentRequestEntity> {
  @Query("SELECT * FROM payment_request")
  suspend fun query(): List<PaymentRequestEntity>

  @Query("SELECT * FROM payment_request")
  fun flowOf(): Flow<List<PaymentRequestEntity>>

  @Query("SELECT * FROM payment_request WHERE account_id=:id AND payment_status=:status")
  fun qByAccountId(
    id: Long,
    status: String = PaymentStatus.Waiting.name,
  ): Flow<List<PaymentRequestEntity>>

  @Query("SELECT * FROM payment_request ORDER BY id DESC LIMIT 1")
  suspend fun getLast(): PaymentRequestEntity

  @Transaction
  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query(
    """
    SELECT acc.*,
    request.*,
    plans.fee AS fee
    FROM payment_request AS request
    JOIN account AS acc ON acc.id = request.account_id
    JOIN payment_method AS method ON method.id = request.payment_method_id
    JOIN payment_plan AS plans ON plans.id = method.payment_plan_id
  """,
  )
  fun qFlowWithAccount(): Flow<List<PaymentRequestWithAccountEntity>>

  @Transaction
  @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
  @Query(
    """
    SELECT acc.*,
    request.*,
    plans.fee AS fee
    FROM payment_request AS request
    JOIN account AS acc ON acc.id = request.account_id
    JOIN payment_method AS method ON method.id = request.payment_method_id
    JOIN payment_plan AS plans ON plans.id = method.payment_plan_id
    WHERE acc.id = :id
    AND request.id NOT IN (SELECT id FROM payment)
  """,
  )
  fun qWithAccountByAccountId(id: Long): Flow<List<PaymentRequestWithAccountEntity>>
}
