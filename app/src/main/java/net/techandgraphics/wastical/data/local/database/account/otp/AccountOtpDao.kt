package net.techandgraphics.wastical.data.local.database.account.otp

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface AccountOtpDao : BaseDao<AccountOtpEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM account_opt")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM account_opt")
  suspend fun query(): List<AccountOtpEntity>

  @Query("DELETE FROM account_opt")
  suspend fun deleteAll()

  @Query("SELECT * FROM account_opt WHERE otp=:otp")
  suspend fun getByOpt(otp: Int): List<AccountOtpEntity>
}
