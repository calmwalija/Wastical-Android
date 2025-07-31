package net.techandgraphics.wastical.data.local.database.payment.pay.month.covered

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao
import net.techandgraphics.wastical.data.remote.payment.PaymentStatus

@Dao interface PaymentMonthCoveredDao : BaseDao<PaymentMonthCoveredEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM payment_month_covered ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM payment_month_covered")
  fun flowOfPaymentMonthCovered(): Flow<List<PaymentMonthCoveredEntity>>

  @Query("SELECT * FROM payment_month_covered WHERE payment_id=:id")
  suspend fun getByPaymentId(id: Long): List<PaymentMonthCoveredEntity>

  @Query("SELECT * FROM payment_month_covered WHERE account_id=:id AND created_at=:at AND month=:month")
  suspend fun getByCreatedAt(id: Long, month: Int, at: Long): PaymentMonthCoveredEntity?

  @Query("SELECT * FROM payment_month_covered GROUP BY month, year")
  suspend fun qGroupByMonth(): List<PaymentMonthCoveredEntity>

  @Query(
    """
    SELECT
    pmc.*
  FROM
    payment p
    JOIN payment_month_covered pmc ON p.id = pmc.payment_id
  WHERE
    p.payment_status = :status
  ORDER BY
    year DESC,
    month DESC
  LIMIT
    1
  """,
  )
  suspend fun getLast(status: String = PaymentStatus.Approved.name): PaymentMonthCoveredEntity?
}
