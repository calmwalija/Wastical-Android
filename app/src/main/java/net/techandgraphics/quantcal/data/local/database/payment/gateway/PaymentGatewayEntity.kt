package net.techandgraphics.quantcal.data.local.database.payment.gateway

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "payment_gateway")
data class PaymentGatewayEntity(
  @PrimaryKey val id: Long,
  val name: String,
  val type: String,
  @ColumnInfo("created_at") val createdAt: Long,
  @ColumnInfo("updated_at") val updatedAt: Long,
)
