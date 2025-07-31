package net.techandgraphics.wastical.data.local.database.demographic.street

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface DemographicStreetDao : BaseDao<DemographicStreetEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM demographic_street ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM demographic_street")
  suspend fun query(): List<DemographicStreetEntity>

  @Query("SELECT * FROM demographic_street WHERE id=:id")
  suspend fun get(id: Long): DemographicStreetEntity

  @Query("SELECT * FROM demographic_street")
  fun flow(): Flow<List<DemographicStreetEntity>>
}
