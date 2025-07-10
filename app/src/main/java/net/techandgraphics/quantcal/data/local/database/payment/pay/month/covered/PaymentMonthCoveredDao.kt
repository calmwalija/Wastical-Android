package net.techandgraphics.quantcal.data.local.database.payment.pay.month.covered

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.quantcal.data.local.database.BaseDao

@Dao interface PaymentMonthCoveredDao : BaseDao<PaymentMonthCoveredEntity> {

  @Query("SELECT * FROM payment_month_covered")
  fun flowOfPaymentMonthCovered(): Flow<List<PaymentMonthCoveredEntity>>

  @Query("SELECT * FROM payment_month_covered WHERE payment_id=:id")
  suspend fun getByPaymentId(id: Long): List<PaymentMonthCoveredEntity>

  @Query("SELECT * FROM payment_month_covered WHERE account_id=:id AND created_at=:at AND month=:month")
  suspend fun getByCreatedAt(id: Long, month: Int, at: Long): PaymentMonthCoveredEntity?

  @Query("SELECT * FROM payment_month_covered GROUP BY month, year")
  suspend fun qGroupByMonth(): List<PaymentMonthCoveredEntity>
}
