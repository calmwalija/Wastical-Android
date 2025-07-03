package net.techandgraphics.quantcal.data.local.database.demographic.area

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "demographic_area")
data class DemographicAreaEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val type: String,
  val description: String,
  val latitude: Float,
  val longitude: Float,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
