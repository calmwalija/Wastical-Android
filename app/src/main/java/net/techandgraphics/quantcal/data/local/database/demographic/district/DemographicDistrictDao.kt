package net.techandgraphics.quantcal.data.local.database.demographic.district

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.quantcal.data.local.database.BaseDao

@Dao
interface DemographicDistrictDao : BaseDao<DemographicDistrictEntity> {
  @Query("SELECT * FROM demographic_district")
  suspend fun query(): List<DemographicDistrictEntity>

  @Query("SELECT * FROM demographic_district")
  fun flow(): Flow<List<DemographicDistrictEntity>>
}
