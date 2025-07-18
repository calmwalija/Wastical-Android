package net.techandgraphics.qgateway.data.local.database.sms

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.qgateway.data.local.database.BaseDao

@Dao
abstract class SmsDao : BaseDao<SmsEntity> {
  @Query("SELECT * FROM sms")
  abstract fun query(): Flow<List<SmsEntity>>

  @Query("SELECT * FROM sms WHERE handshake = 0")
  abstract fun qHandshake(): Flow<List<SmsEntity>>
}
