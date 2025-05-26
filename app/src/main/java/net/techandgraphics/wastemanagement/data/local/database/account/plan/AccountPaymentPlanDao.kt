package net.techandgraphics.wastemanagement.data.local.database.account.plan

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface AccountPaymentPlanDao : BaseDao<AccountPaymentPlanEntity> {
  @Query("SELECT * FROM account_payment_plan")
  suspend fun query(): List<AccountPaymentPlanEntity>

  @Query("SELECT * FROM account_payment_plan")
  fun flow(): Flow<List<AccountPaymentPlanEntity>>
}
