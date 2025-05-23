package net.techandgraphics.wastemanagement.data.local.database.company

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface CompanyDao : BaseDao<CompanyEntity> {
  @Query("SELECT * FROM company")
  suspend fun query(): List<CompanyEntity>

  @Query("SELECT * FROM company")
  fun flow(): Flow<List<CompanyEntity>>
}
