package net.techandgraphics.wastemanagement.data.local.database.company.contact

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity

@Entity(
  tableName = "company_contact",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
    ),
  ],
  indices = [Index("company_id")],
)
data class CompanyContactEntity(
  val id: Long,
  val email: String?,
  val contact: String,
  val primary: Boolean,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long?,
)
