package net.techandgraphics.wastemanagement.data.local.database.demographic.area

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface DemographicAreaDao : BaseDao<DemographicAreaEntity> {
  @Query("SELECT * FROM demographic_area")
  suspend fun query(): List<DemographicAreaEntity>

  @Query("SELECT * FROM demographic_area")
  fun flow(): Flow<List<DemographicAreaEntity>>
}
