package net.techandgraphics.wastemanagement.data.local.database.payment.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity

@Entity(
  tableName = "payment_plan",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
    ),
  ],
  indices = [Index("company_id")],
)
data class PaymentPlanEntity(
  @PrimaryKey val id: Long,
  val fee: Int,
  val name: String,
  val period: String,
  val status: String,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long?,
)
