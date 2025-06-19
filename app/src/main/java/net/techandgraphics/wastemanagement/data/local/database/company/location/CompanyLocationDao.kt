package net.techandgraphics.wastemanagement.data.local.database.company.location

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.local.database.relations.CompanyLocationWithDemographicEntity

@Dao
interface CompanyLocationDao : BaseDao<CompanyLocationEntity> {
  @Query("SELECT * FROM company_location")
  suspend fun query(): List<CompanyLocationEntity>

  @Query("SELECT * FROM company_location  WHERE id=:id")
  suspend fun get(id: Long): CompanyLocationEntity

  @Query("SELECT * FROM company_location  WHERE demographic_street_id=:id")
  suspend fun getByStreetId(id: Long): CompanyLocationEntity

  @Query("SELECT * FROM company_location")
  fun flow(): Flow<List<CompanyLocationEntity>>

  @Transaction
  @Query("SELECT * FROM company_location")
  suspend fun qWithDemographic(): List<CompanyLocationWithDemographicEntity>
}
