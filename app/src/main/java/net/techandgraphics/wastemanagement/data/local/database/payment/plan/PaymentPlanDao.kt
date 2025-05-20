package net.techandgraphics.wastemanagement.data.local.database.payment.plan

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface PaymentPlanDao : BaseDao<PaymentPlanEntity> {
  @Query("SELECT * FROM payment_plan")
  abstract fun query(): Flow<List<PaymentPlanEntity>>
}
