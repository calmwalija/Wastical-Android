package net.techandgraphics.wastical.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wastical.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastical.data.local.database.payment.method.PaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.payment.plan.PaymentPlanEntity

data class PaymentMethodWithGatewayAndPlanEntity(
  @Embedded val method: PaymentMethodEntity,
  @Relation(
    entity = PaymentGatewayEntity::class,
    parentColumn = "payment_gateway_id",
    entityColumn = "id",
  ) val gateway: PaymentGatewayEntity,
  @Relation(
    entity = PaymentPlanEntity::class,
    parentColumn = "payment_plan_id",
    entityColumn = "id",
  ) val plan: PaymentPlanEntity,
)
