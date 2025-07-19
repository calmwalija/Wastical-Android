package net.techandgraphics.qgateway.data.local.database.otp

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.qgateway.data.local.database.BaseDao
import net.techandgraphics.qgateway.data.local.database.account.AccountWithOtpEntity

@Dao
interface OtpDao : BaseDao<OtpEntity> {

  @Transaction
  @Query("SELECT * FROM account_opt")
  fun flowOf(): Flow<List<AccountWithOtpEntity>>

  @Query("SELECT * FROM account_opt WHERE sent = 0")
  suspend fun qNotSent(): List<OtpEntity>

  @Query("SELECT * FROM account_opt ORDER BY updated_at DESC LIMIT 1")
  suspend fun getByUpdatedAtLatest(): OtpEntity?
}
