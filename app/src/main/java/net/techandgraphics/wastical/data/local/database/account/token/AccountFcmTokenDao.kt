package net.techandgraphics.wastical.data.local.database.account.token

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
interface AccountFcmTokenDao : BaseDao<AccountFcmTokenEntity> {
  @Query("SELECT * FROM account_fcm_token")
  suspend fun query(): List<AccountFcmTokenEntity>

  @Query("SELECT * FROM account_fcm_token")
  fun flow(): Flow<List<AccountFcmTokenEntity>>

  @Query("DELETE FROM account_fcm_token")
  suspend fun deleteAll()
}
