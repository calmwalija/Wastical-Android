package net.techandgraphics.wastemanagement.data.local.database.payment.collection

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.company.CompanyEntity

@Entity(
  tableName = "payment_collection_day",
  foreignKeys = [
    ForeignKey(
      entity = CompanyEntity::class,
      parentColumns = ["id"],
      childColumns = ["company_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("company_id"),
  ],
)
data class PaymentCollectionDayEntity(
  @PrimaryKey val id: Long,
  val day: Int,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
