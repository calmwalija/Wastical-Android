package net.techandgraphics.wastical.data.local.database.notification.request

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
interface NotificationRequestDao : BaseDao<NotificationRequestEntity> {

  @Query("SELECT * FROM notification_request")
  suspend fun query(): List<NotificationRequestEntity>

  @Query("DELETE FROM notification_request WHERE uuid=:uuid")
  suspend fun deleteByUuid(uuid: String)
}
