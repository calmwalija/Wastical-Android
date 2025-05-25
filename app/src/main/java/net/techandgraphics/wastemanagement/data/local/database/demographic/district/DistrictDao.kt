package net.techandgraphics.wastemanagement.data.local.database.demographic.district

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface DistrictDao : BaseDao<DistrictEntity> {
  @Query("SELECT * FROM demographic_district")
  suspend fun query(): List<DistrictEntity>

  @Query("SELECT * FROM demographic_district")
  fun flow(): Flow<List<DistrictEntity>>
}
