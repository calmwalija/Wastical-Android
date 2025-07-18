package net.techandgraphics.qgateway.data.local.database.otp

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.qgateway.data.local.database.BaseDao

@Dao
interface OtpDao : BaseDao<OtpEntity> {

  @Query("SELECT * FROM account_opt WHERE sent = 0")
  suspend fun qNotSent(): List<OtpEntity>

  @Query("SELECT * FROM account_opt ORDER BY updated_at DESC LIMIT 1")
  suspend fun getByUpdatedAtLatest(): OtpEntity?
}
