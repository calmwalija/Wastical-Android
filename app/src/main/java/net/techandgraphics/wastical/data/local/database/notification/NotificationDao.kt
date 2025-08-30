package net.techandgraphics.wastical.data.local.database.notification

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.AccountRole
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
abstract class NotificationDao : BaseDao<NotificationEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM notification ORDER BY updated_at DESC LIMIT 1")
  abstract override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM notification WHERE sync_status = 0")
  abstract fun flowOfSync(): Flow<List<NotificationEntity>>

  @Query("SELECT * FROM notification WHERE sync_status = :status AND recipient_role=:role ")
  abstract fun flowOfSync(
    status: Int = NotificationSyncStatus.Sync.ordinal,
    role: String = AccountRole.Company.name,
  ): Flow<List<NotificationEntity>>

  @Query(
    """
    SELECT * FROM notification
    WHERE
    recipient_role=:role AND
    (notification.body LIKE '%' || :query || '%'
    OR notification.title LIKE '%' || :query || '%')
    ORDER BY CASE WHEN :sort THEN notification.created_at END DESC,
    CASE WHEN :sort = 0 THEN notification.created_at END ASC
  """,
  )
  abstract fun flowOf(
    query: String = "",
    sort: Boolean = true,
    role: String = AccountRole.Client.name,
  ): Flow<List<NotificationEntity>>

  @Query(
    """
    SELECT * FROM notification
    WHERE
    recipient_role=:role AND
    (notification.body LIKE '%' || :query || '%'
    OR notification.title LIKE '%' || :query || '%')
    ORDER BY CASE WHEN :sort THEN notification.created_at END DESC,
    CASE WHEN :sort = 0 THEN notification.created_at END ASC
  """,
  )
  abstract fun flowOfPaging(
    query: String = "",
    sort: Boolean = true,
    role: String = AccountRole.Company.name,
  ): PagingSource<Int, NotificationEntity>

  @Query("SELECT * FROM notification WHERE id = :id")
  abstract fun get(id: Long): NotificationEntity?
}
