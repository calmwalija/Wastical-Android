package net.techandgraphics.wastemanagement.data.local.database.payment.pay.request

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomWarnings
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.local.database.relations.PaymentRequestWithAccountEntity

@Dao interface PaymentRequestDao : BaseDao<PaymentRequestEntity> {
  @Query("SELECT * FROM payment_request")
  suspend fun query(): List<PaymentRequestEntity>

  @Query("SELECT * FROM payment_request WHERE account_id=:id")
  fun qByAccountId(id: Long): Flow<List<PaymentRequestEntity>>

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
  """,
  )
  fun getWithAccountByAccountId(id: Long): Flow<List<PaymentRequestWithAccountEntity>>
}
