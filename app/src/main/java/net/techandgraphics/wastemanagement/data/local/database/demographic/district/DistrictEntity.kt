package net.techandgraphics.wastemanagement.data.local.database.demographic.district

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "demographic_district")
data class DistrictEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val region: String,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long?,
)
