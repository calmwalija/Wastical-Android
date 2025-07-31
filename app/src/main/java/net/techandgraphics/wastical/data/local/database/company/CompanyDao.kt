package net.techandgraphics.wastical.data.local.database.company

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface CompanyDao : BaseDao<CompanyEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM company ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM company")
  suspend fun query(): List<CompanyEntity>

  @Query("SELECT * FROM company")
  fun flow(): Flow<List<CompanyEntity>>
}
