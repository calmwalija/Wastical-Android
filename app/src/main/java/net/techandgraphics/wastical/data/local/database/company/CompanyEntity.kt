package net.techandgraphics.wastical.data.local.database.company

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.Status

@Entity(tableName = "company")
data class CompanyEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val email: String,
  val address: String,
  val slogan: String,
  val latitude: Float = -1f,
  val longitude: Float = -1f,
  val status: Status = Status.Active,
  @ColumnInfo(name = "updated_at") val updatedAt: Long,
  @ColumnInfo(name = "created_at") val createdAt: Long,
)
