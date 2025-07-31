package net.techandgraphics.wastical.data.local.database.company.bin.collection

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface CompanyBinCollectionDao : BaseDao<CompanyBinCollectionEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM company_bin_collection ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM company_bin_collection")
  suspend fun query(): List<CompanyBinCollectionEntity>

  @Query("SELECT * FROM company_bin_collection")
  fun flow(): Flow<List<CompanyBinCollectionEntity>>

  @Query("SELECT * FROM company_bin_collection WHERE id =:id")
  suspend fun get(id: Long): CompanyBinCollectionEntity

//  @Transaction
//  @Query("SELECT * FROM company_bin_collection WHERE company_id =:companyId")
//  fun flowOfTxn(companyId: Long): Flow<List<TrashCompanyStreetEntity>>

//  @Transaction
//  @Query("SELECT * FROM company_bin_collection LIMIT 1")
//  suspend fun getByStreetId(id: Long): TrashCompanyStreetEntity
}
//
// data class TrashCompanyStreetEntity(
//  @Embedded val tCSEntity: CompanyBinCollectionEntity,
//  @Relation(
//    entity = DemographicStreetEntity::class,
//    parentColumn = "street_id",
//    entityColumn = "id",
//  ) val streetEntity: DemographicStreetEntity,
//  @Relation(
//    entity = CompanyEntity::class,
//    parentColumn = "company_id",
//    entityColumn = "id",
//  ) val companyEntity: CompanyEntity,
// )
