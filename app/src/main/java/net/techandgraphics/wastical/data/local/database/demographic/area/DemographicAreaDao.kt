package net.techandgraphics.wastical.data.local.database.demographic.area

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface DemographicAreaDao : BaseDao<DemographicAreaEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM demographic_area ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM demographic_area")
  suspend fun query(): List<DemographicAreaEntity>

  @Query("SELECT * FROM demographic_area WHERE id=:id")
  suspend fun get(id: Long): DemographicAreaEntity

  @Query("SELECT * FROM demographic_area")
  fun flow(): Flow<List<DemographicAreaEntity>>
}
