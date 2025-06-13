package net.techandgraphics.wastemanagement.data.local.database.company.location

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface CompanyLocationDao : BaseDao<CompanyLocationEntity> {
  @Query("SELECT * FROM company_location")
  suspend fun query(): List<CompanyLocationEntity>

  @Query("SELECT * FROM company_location")
  fun flow(): Flow<List<CompanyLocationEntity>>
}
