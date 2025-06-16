package net.techandgraphics.wastemanagement.data.local.database.payment.method

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.local.database.relations.PaymentMethodWithGatewayEntity

@Dao
interface PaymentMethodDao : BaseDao<PaymentMethodEntity> {
  @Query("SELECT * FROM payment_method")
  suspend fun query(): List<PaymentMethodEntity>

  @Transaction
  @Query("SELECT * FROM payment_method GROUP BY account")
  suspend fun qWithGateway(): List<PaymentMethodWithGatewayEntity>

  @Query("SELECT * FROM payment_method")
  fun flow(): Flow<List<PaymentMethodEntity>>

  @Query("SELECT * FROM payment_method WHERE id=:id")
  suspend fun get(id: Long): PaymentMethodEntity

  @Query("SELECT * FROM payment_method WHERE payment_plan_id=:id")
  suspend fun getByPaymentPlanId(id: Long): List<PaymentMethodEntity>

  @Transaction
  @Query("SELECT * FROM payment_method WHERE payment_plan_id=:id")
  suspend fun qWithGatewayByPaymentPlanId(id: Long): List<PaymentMethodWithGatewayEntity>

  @Transaction
  @Query("SELECT * FROM payment_method WHERE id=:id")
  suspend fun getWithGatewayById(id: Long): PaymentMethodWithGatewayEntity
}
