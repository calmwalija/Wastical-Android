package net.techandgraphics.wastical.data.local.database.account.plan

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface AccountPaymentPlanDao : BaseDao<AccountPaymentPlanEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM account_payment_plan ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM account_payment_plan")
  suspend fun query(): List<AccountPaymentPlanEntity>

  @Query("SELECT * FROM account_payment_plan WHERE account_id=:id")
  suspend fun getByAccountId(id: Long): AccountPaymentPlanEntity

  @Query("SELECT * FROM account_payment_plan")
  fun flow(): Flow<List<AccountPaymentPlanEntity>>
}
