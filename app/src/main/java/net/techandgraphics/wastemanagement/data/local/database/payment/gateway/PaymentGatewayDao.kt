package net.techandgraphics.wastemanagement.data.local.database.payment.gateway

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface PaymentGatewayDao : BaseDao<PaymentGatewayEntity> {
  @Query("SELECT * FROM payment_gateway")
  suspend fun query(): List<PaymentGatewayEntity>

  @Query("SELECT * FROM payment_gateway")
  fun flow(): Flow<List<PaymentGatewayEntity>>

  @Query("SELECT * FROM payment_gateway WHERE id=:id")
  suspend fun get(id: Long): PaymentGatewayEntity
}
