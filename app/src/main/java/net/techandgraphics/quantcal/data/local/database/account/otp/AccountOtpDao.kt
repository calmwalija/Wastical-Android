package net.techandgraphics.quantcal.data.local.database.account.otp

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.quantcal.data.local.database.BaseDao

@Dao
interface AccountOtpDao : BaseDao<AccountOtpEntity> {

  @Query("SELECT * FROM account_opt")
  suspend fun query(): List<AccountOtpEntity>

  @Query("DELETE FROM account_opt")
  suspend fun deleteAll()

  @Query("SELECT * FROM account_opt WHERE otp=:otp")
  suspend fun getByOpt(otp: Int): List<AccountOtpEntity>
}
