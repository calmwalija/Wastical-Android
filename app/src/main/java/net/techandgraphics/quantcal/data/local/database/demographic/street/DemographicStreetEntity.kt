package net.techandgraphics.quantcal.data.local.database.demographic.street

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "demographic_street")
data class DemographicStreetEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val latitude: Float,
  val longitude: Float,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
