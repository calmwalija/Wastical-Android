package net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.DemographicStreetEntity

@Dao
interface TrashCollectionScheduleDao : BaseDao<TrashCollectionScheduleEntity> {
  @Query("SELECT * FROM company_trash_collection_schedule")
  suspend fun query(): List<TrashCollectionScheduleEntity>

  @Query("SELECT * FROM company_trash_collection_schedule")
  fun flow(): Flow<List<TrashCollectionScheduleEntity>>

  @Query("SELECT * FROM company_trash_collection_schedule WHERE id =:id")
  suspend fun get(id: Long): TrashCollectionScheduleEntity

  @Transaction
  @Query("SELECT * FROM company_trash_collection_schedule WHERE company_id =:companyId")
  fun flowOfTxn(companyId: Long): Flow<List<TrashCompanyStreetEntity>>

  @Transaction
  @Query("SELECT * FROM company_trash_collection_schedule WHERE street_id =:id")
  suspend fun getByStreetId(id: Long): TrashCompanyStreetEntity
}

data class TrashCompanyStreetEntity(
  @Embedded val tCSEntity: TrashCollectionScheduleEntity,
  @Relation(
    entity = DemographicStreetEntity::class,
    parentColumn = "street_id",
    entityColumn = "id",
  ) val streetEntity: DemographicStreetEntity,
  @Relation(
    entity = CompanyEntity::class,
    parentColumn = "company_id",
    entityColumn = "id",
  ) val companyEntity: CompanyEntity,
)
