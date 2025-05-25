package net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface TrashCollectionScheduleDao : BaseDao<TrashCollectionScheduleEntity> {
  @Query("SELECT * FROM company_trash_collection_schedule")
  suspend fun query(): List<TrashCollectionScheduleEntity>

  @Query("SELECT * FROM company_trash_collection_schedule")
  fun flow(): Flow<List<TrashCollectionScheduleEntity>>
}
