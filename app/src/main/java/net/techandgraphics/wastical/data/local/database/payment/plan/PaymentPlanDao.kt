package net.techandgraphics.wastical.data.local.database.payment.plan

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface PaymentPlanDao : BaseDao<PaymentPlanEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM payment ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM payment_plan ORDER BY fee")
  suspend fun query(): List<PaymentPlanEntity>

  @Query("SELECT * FROM payment_plan ORDER BY fee")
  fun flow(): Flow<List<PaymentPlanEntity>>

  @Query("SELECT * FROM payment_plan WHERE id=:id")
  suspend fun get(id: Long): PaymentPlanEntity
}
