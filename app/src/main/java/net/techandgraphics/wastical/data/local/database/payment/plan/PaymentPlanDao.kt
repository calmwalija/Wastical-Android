package net.techandgraphics.wastical.data.local.database.payment.plan

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
interface PaymentPlanDao : BaseDao<PaymentPlanEntity> {
  @Query("SELECT * FROM payment_plan ORDER BY fee")
  suspend fun query(): List<PaymentPlanEntity>

  @Query("SELECT * FROM payment_plan ORDER BY fee")
  fun flow(): Flow<List<PaymentPlanEntity>>

  @Query("SELECT * FROM payment_plan WHERE id=:id")
  suspend fun get(id: Long): PaymentPlanEntity
}
