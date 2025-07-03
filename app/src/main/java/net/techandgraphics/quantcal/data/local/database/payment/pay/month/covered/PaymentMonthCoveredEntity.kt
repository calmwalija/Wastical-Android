package net.techandgraphics.quantcal.data.local.database.payment.pay.month.covered

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.quantcal.data.local.database.account.AccountEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity

@Entity(
  tableName = "payment_month_covered",
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["account_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PaymentEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("account_id"),
    Index("payment_id"),
  ],
)
data class PaymentMonthCoveredEntity(
  @PrimaryKey val id: Long,
  val month: Int,
  val year: Int,
  @ColumnInfo("payment_id") val paymentId: Long,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
