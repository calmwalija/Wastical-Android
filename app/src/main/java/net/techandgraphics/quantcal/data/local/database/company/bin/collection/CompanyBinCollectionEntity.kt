package net.techandgraphics.quantcal.data.local.database.company.bin.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.quantcal.data.local.database.company.CompanyEntity
import net.techandgraphics.quantcal.data.local.database.company.location.CompanyLocationEntity

@Entity(
  tableName = "company_bin_collection",
  foreignKeys = [
    ForeignKey(
      entity = CompanyLocationEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_location_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("company_location_id"),
    Index("company_id"),
  ],
)
data class CompanyBinCollectionEntity(
  @PrimaryKey val id: Long,
  @ColumnInfo("day_of_week") val dayOfWeek: String,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("company_location_id") val companyLocationId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
