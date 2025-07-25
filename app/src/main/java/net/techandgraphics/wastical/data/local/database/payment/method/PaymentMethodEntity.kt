package net.techandgraphics.wastical.data.local.database.payment.method

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import net.techandgraphics.wastical.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastical.data.local.database.payment.plan.PaymentPlanEntity

@Entity(
  tableName = "payment_method",
  foreignKeys = [
    ForeignKey(
      entity = PaymentPlanEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_plan_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
    ForeignKey(
      entity = PaymentGatewayEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_gateway_id"],
      onUpdate = ForeignKey.CASCADE,
      onDelete = ForeignKey.CASCADE,
    ),
  ],
  indices = [
    Index("payment_plan_id"),
    Index("payment_gateway_id"),
  ],
)
data class PaymentMethodEntity(
  @PrimaryKey val id: Long,
  val account: String,
  val isSelected: Boolean = false,
  @ColumnInfo("payment_plan_id") val paymentPlanId: Long,
  @ColumnInfo("payment_gateway_id") val paymentGatewayId: Long,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
