package net.techandgraphics.wastemanagement.data.local.database.company

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface CompanyDao : BaseDao<CompanyEntity> {
  @Query("SELECT * FROM company")
  suspend fun query(): List<CompanyEntity>
}
