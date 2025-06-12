package net.techandgraphics.wastemanagement.data.local.database.payment.pay

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import net.techandgraphics.wastemanagement.data.local.database.account.AccountEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.plan.PaymentPlanEntity

@Entity(
  tableName = "payment",
  foreignKeys = [
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["account_id"],
    ),
    ForeignKey(
      entity = AccountEntity::class,
      parentColumns = ["id"],
      childColumns = ["executed_by_id"],
    ),
    ForeignKey(
      entity = PaymentMethodEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_method_id"],
    ),
    ForeignKey(
      entity = PaymentGatewayEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_gateway_id"],
    ),
    ForeignKey(
      entity = PaymentPlanEntity::class,
      parentColumns = ["id"],
      childColumns = ["payment_plan_id"],
    ),
  ],
  indices = [
    Index("account_id"),
    Index("payment_method_id"),
    Index("payment_plan_id"),
    Index("payment_gateway_id"),
    Index("executed_by_id"),
  ],
)
data class PaymentEntity(
  @PrimaryKey val id: Long,
  val months: Int = -1,
  @ColumnInfo("screenshot_text") val screenshotText: String,
  @ColumnInfo("transaction_id") val transactionId: String,
  @ColumnInfo("payment_method_id") val paymentMethodId: Long,
  @ColumnInfo("account_id") val accountId: Long,
  @ColumnInfo("payment_status") val status: String,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
  @ColumnInfo("company_id") val companyId: Long,
  @ColumnInfo("executed_by_id") val executedById: Long,
  @ColumnInfo("payment_plan_id") val paymentPlanId: Long,
  @ColumnInfo("payment_plan_fee") val paymentPlanFee: Int,
  @ColumnInfo("payment_plan_period") val paymentPlanPeriod: String,
  @ColumnInfo("payment_gateway_id") val paymentGatewayId: Long,
  @ColumnInfo("payment_gateway_name") val paymentGatewayName: String,
  @ColumnInfo("payment_gateway_type") val paymentGatewayType: String,
)

data class PaymentAccountEntity(
  @Embedded val payment: PaymentEntity,
  @Relation(
    entity = AccountEntity::class,
    parentColumn = "account_id",
    entityColumn = "id",
  ) val account: AccountEntity,
)
