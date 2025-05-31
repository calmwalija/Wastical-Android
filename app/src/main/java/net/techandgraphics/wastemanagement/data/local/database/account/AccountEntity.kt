package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.company.trash.collection.schedule.TrashCollectionScheduleEntity

@Entity(
  tableName = "account",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
    ),
    ForeignKey(
      entity = TrashCollectionScheduleEntity::class,
      parentColumns = ["id"],
      childColumns = ["trash_collection_schedule_id"],
    ),
  ],
  indices = [
    Index("trash_collection_schedule_id"),
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
  @ColumnInfo("trash_collection_schedule_id") val trashCollectionScheduleId: Long,
  @ColumnInfo(name = "company_id") val companyId: Long,
  @ColumnInfo(name = "leaving_reason") val leavingReason: String? = null,
  @ColumnInfo(name = "leaving_timestamp") val leavingTimestamp: Long?,
  @ColumnInfo(name = "updated_at") val updatedAt: Long,
  @ColumnInfo(name = "created_at") val createdAt: Long,
)
