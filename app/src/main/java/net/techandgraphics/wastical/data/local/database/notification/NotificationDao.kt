package net.techandgraphics.wastical.data.local.database.notification

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
abstract class NotificationDao : BaseDao<NotificationEntity> {

  @Query("SELECT * FROM notification WHERE sync_status = 0")
  abstract fun flowOfSync(): Flow<List<NotificationEntity>>

  @Query("SELECT * FROM notification ORDER BY id DESC")
  abstract fun flowOf(): Flow<List<NotificationEntity>>

  @Query(
    """
    SELECT * FROM notification
    WHERE (notification.body LIKE '%' || :query || '%'
    OR notification.title LIKE '%' || :query || '%')
    ORDER BY CASE WHEN :sort THEN notification.created_at END DESC,
    CASE WHEN :sort = 0 THEN notification.created_at END ASC
  """,
  )
  abstract fun flowOfPaging(
    query: String = "",
    sort: Boolean = true,
  ): PagingSource<Int, NotificationEntity>

  @Query("SELECT * FROM notification WHERE id = :id")
  abstract fun get(id: Long): NotificationEntity?
}
