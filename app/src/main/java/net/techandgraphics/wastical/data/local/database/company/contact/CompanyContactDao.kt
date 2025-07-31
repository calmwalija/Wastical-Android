package net.techandgraphics.wastical.data.local.database.company.contact

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface CompanyContactDao : BaseDao<CompanyContactEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM company_contact ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM company_contact")
  suspend fun query(): List<CompanyContactEntity>

  @Query("SELECT * FROM company_contact")
  fun flow(): Flow<List<CompanyContactEntity>>
}
