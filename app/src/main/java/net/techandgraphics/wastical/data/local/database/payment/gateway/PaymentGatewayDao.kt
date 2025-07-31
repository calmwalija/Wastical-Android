package net.techandgraphics.wastical.data.local.database.payment.gateway

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface PaymentGatewayDao : BaseDao<PaymentGatewayEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM payment_gateway ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM payment_gateway")
  suspend fun query(): List<PaymentGatewayEntity>

  @Query("SELECT * FROM payment_gateway")
  fun flow(): Flow<List<PaymentGatewayEntity>>

  @Query("SELECT * FROM payment_gateway WHERE id=:id")
  suspend fun get(id: Long): PaymentGatewayEntity
}
