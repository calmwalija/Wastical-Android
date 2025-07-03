package net.techandgraphics.quantcal.data.local.database.demographic.street

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.quantcal.data.local.database.BaseDao

@Dao
interface DemographicStreetDao : BaseDao<DemographicStreetEntity> {
  @Query("SELECT * FROM demographic_street")
  suspend fun query(): List<DemographicStreetEntity>

  @Query("SELECT * FROM demographic_street WHERE id=:id")
  suspend fun get(id: Long): DemographicStreetEntity

  @Query("SELECT * FROM demographic_street")
  fun flow(): Flow<List<DemographicStreetEntity>>
}
