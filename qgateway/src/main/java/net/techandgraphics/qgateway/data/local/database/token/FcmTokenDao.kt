package net.techandgraphics.qgateway.data.local.database.token

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.qgateway.data.local.database.BaseDao

@Dao
interface FcmTokenDao : BaseDao<FcmTokenEntity> {
  @Query("SELECT * FROM fcm_token")
  suspend fun query(): List<FcmTokenEntity>

  @Query("SELECT * FROM fcm_token")
  fun flow(): Flow<List<FcmTokenEntity>>

  @Query("DELETE FROM fcm_token")
  suspend fun deleteAll()
}
