package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.location.CompanyLocationEntity

@Entity(
  tableName = "account",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
    ),
    ForeignKey(
      entity = CompanyLocationEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_location_id"],
    ),
  ],
  indices = [
    Index("company_location_id"),
    Index("company_id"),
  ],
)
data class AccountEntity(
  @PrimaryKey val id: Long,
  val uuid: String,
  val title: String,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String?,
  val latitude: Float = -1f,
  val longitude: Float = -1f,
  val status: String,
  @ColumnInfo("company_location_id") val companyLocationId: Long,
  @ColumnInfo(name = "company_id") val companyId: Long,
  @ColumnInfo(name = "leaving_reason") val leavingReason: String? = null,
  @ColumnInfo(name = "leaving_timestamp") val leavingTimestamp: Long?,
  @ColumnInfo(name = "updated_at") val updatedAt: Long,
  @ColumnInfo(name = "created_at") val createdAt: Long,
)
