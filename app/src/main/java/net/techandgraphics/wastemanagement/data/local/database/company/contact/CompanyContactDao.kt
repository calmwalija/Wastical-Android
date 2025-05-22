package net.techandgraphics.wastemanagement.data.local.database.company.contact

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface CompanyContactDao : BaseDao<CompanyContactEntity> {
  @Query("SELECT * FROM company_contact")
  suspend fun query(): List<CompanyContactEntity>
}
