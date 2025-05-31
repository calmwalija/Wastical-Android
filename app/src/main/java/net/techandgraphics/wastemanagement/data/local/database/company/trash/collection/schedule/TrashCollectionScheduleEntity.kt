package net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.StreetEntity

@Entity(
  tableName = "company_trash_collection_schedule",
  foreignKeys = [
    ForeignKey(
      entity = StreetEntity::class,
      parentColumns = ["id"],
      childColumns = ["street_id"],
    ),
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
    ),
  ],
  indices = [
    Index("street_id"),
    Index("company_id"),
  ],
)
data class TrashCollectionScheduleEntity(
  @PrimaryKey val id: Long,
  @ColumnInfo("day_of_week") val dayOfWeek: String,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("street_id") val streetId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
