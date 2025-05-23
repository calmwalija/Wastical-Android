package net.techandgraphics.wastemanagement.data.local.database.payment.plan

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface PaymentPlanDao : BaseDao<PaymentPlanEntity> {
  @Query("SELECT * FROM payment_plan")
  suspend fun query(): List<PaymentPlanEntity>

  @Query("SELECT * FROM payment_plan")
  fun flow(): Flow<List<PaymentPlanEntity>>
}
