package net.techandgraphics.quantcal.data.local.database.account.plan.request

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.quantcal.data.local.database.BaseDao

@Dao interface AccountPaymentPlanRequestDao : BaseDao<AccountPaymentPlanRequestEntity> {

  @Query("SELECT * FROM account_payment_plan_request")
  suspend fun query(): List<AccountPaymentPlanRequestEntity>

  @Query("SELECT * FROM account_payment_plan_request")
  fun flowOf(): Flow<List<AccountPaymentPlanRequestEntity>>

  @Query("DELETE FROM account_payment_plan_request WHERE account_id=:id")
  suspend fun deleteByAccountId(id: Long)
}
