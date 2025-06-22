package net.techandgraphics.wastemanagement.data.local.database.account.plan

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity

@Entity(
  tableName = "account_payment_plan",
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["account_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PaymentPlanEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_plan_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("payment_plan_id"),
    Index("account_id"),
  ],
)
data class AccountPaymentPlanEntity(
  @PrimaryKey val id: Long,
  @ColumnInfo("account_uuid") val accountUuid: String,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("payment_plan_id") val paymentPlanId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
