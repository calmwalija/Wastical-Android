package net.techandgraphics.wastical.data.local.database.notification.template

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
abstract class NotificationTemplateDao : BaseDao<NotificationTemplateEntity> {

  @Query("SELECT * FROM notification_template WHERE scope = :scope ORDER BY id DESC")
  abstract fun flowOf(scope: String): Flow<List<NotificationTemplateEntity>>

  @Query("SELECT COUNT(*) FROM notification_template")
  abstract suspend fun count(): Int
}
