package net.techandgraphics.wastemanagement.data.local.database.company.contact

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface CompanyContactDao : BaseDao<CompanyContactEntity> {
  @Query("SELECT * FROM company_contact")
  suspend fun query(): List<CompanyContactEntity>

  @Query("SELECT * FROM company_contact")
  fun flow(): Flow<List<CompanyContactEntity>>
}
