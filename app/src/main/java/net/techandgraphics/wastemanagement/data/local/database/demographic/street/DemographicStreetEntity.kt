package net.techandgraphics.wastemanagement.data.local.database.demographic.street

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.demographic.area.DemographicAreaEntity

@Entity(
  tableName = "demographic_street",
  foreignKeys = [
    ForeignKey(
      entity = DemographicAreaEntity::class,
      parentColumns = ["id"],
      childColumns = ["area_id"],
    ),
  ],
  indices = [Index("area_id")],
)
data class DemographicStreetEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val latitude: Float,
  val longitude: Float,
  @ColumnInfo("area_id") val areaId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
