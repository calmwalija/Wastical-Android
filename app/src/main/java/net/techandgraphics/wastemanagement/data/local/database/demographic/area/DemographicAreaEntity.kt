package net.techandgraphics.wastemanagement.data.local.database.demographic.area

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.demographic.district.DemographicDistrictEntity

@Entity(
  tableName = "demographic_area",
  foreignKeys = [
    ForeignKey(
      entity = DemographicDistrictEntity::class,
      parentColumns = ["id"],
      childColumns = ["district_id"],
    ),
  ],
  indices = [Index("district_id")],
)
data class DemographicAreaEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val type: String,
  val description: String,
  val latitude: Float,
  val longitude: Float,
  @ColumnInfo("district_id") val districtId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
