package net.techandgraphics.wastical.data.local.database.company.location

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.company.CompanyEntity
import net.techandgraphics.wastical.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.wastical.data.local.database.demographic.district.DemographicDistrictEntity
import net.techandgraphics.wastical.data.local.database.demographic.street.DemographicStreetEntity

@Entity(
  tableName = "company_location",
  foreignKeys = [
    ForeignKey(
      entity = DemographicStreetEntity::class,
      parentColumns = ["id"],
      childColumns = ["demographic_street_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = DemographicAreaEntity::class,
      parentColumns = ["id"],
      childColumns = ["demographic_area_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = DemographicDistrictEntity::class,
      parentColumns = ["id"],
      childColumns = ["demographic_district_id"],
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
    Index("demographic_street_id"),
    Index("demographic_area_id"),
    Index("demographic_district_id"),
    Index("company_id"),
  ],
)
data class CompanyLocationEntity(
  @PrimaryKey val id: Long,
  val status: String,
  val uuid: String,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("demographic_street_id") val demographicStreetId: Long,
  @ColumnInfo("demographic_area_id") val demographicAreaId: Long,
  @ColumnInfo("demographic_district_id") val demographicDistrictId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
