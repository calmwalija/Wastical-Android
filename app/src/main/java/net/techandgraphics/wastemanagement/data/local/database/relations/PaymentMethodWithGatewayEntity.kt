package net.techandgraphics.wastemanagement.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wastemanagement.data.local.database.payment.gateway.PaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.payment.method.PaymentMethodEntity

data class PaymentMethodWithGatewayEntity(
  @Embedded val method: PaymentMethodEntity,
  @Relation(
    entity = PaymentGatewayEntity::class,
    parentColumn = "payment_gateway_id",
    entityColumn = "id",
  ) val gateway: PaymentGatewayEntity,
)
