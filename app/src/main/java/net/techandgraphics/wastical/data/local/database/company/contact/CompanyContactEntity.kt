package net.techandgraphics.wastical.data.local.database.company.contact

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.company.CompanyEntity

@Entity(
  tableName = "company_contact",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [Index("company_id")],
)
data class CompanyContactEntity(
  @PrimaryKey val id: Long,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
