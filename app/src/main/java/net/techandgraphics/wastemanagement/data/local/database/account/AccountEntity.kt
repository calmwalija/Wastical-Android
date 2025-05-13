package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.enums.Status
import net.techandgraphics.wastemanagement.data.local.database.enums.Title

@Entity(
  tableName = "account",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
    ),
  ],
  indices = [Index("company_id")],
)
data class AccountEntity(
  @PrimaryKey val id: Long,
  val uuid: String,
  val title: Title,
  val firstname: String,
  val lastname: String,
  val username: String,
  val email: String,
  @ColumnInfo(name = "contact_number") val contactNumber: String,
  @ColumnInfo(name = "company_id") val companyId: Long,
  val latitude: Float = -1f,
  val longitude: Float = -1f,
  val status: Status = Status.Active,
  @ColumnInfo(name = "leaving_reason") val leavingReason: String? = null,
  @ColumnInfo(name = "leaving_timestamp") val leavingTimestamp: Long,
  @ColumnInfo(name = "updated_at") val updatedAt: Long? = null,
  @ColumnInfo(name = "created_at") val createdAt: Long,
)
