package net.techandgraphics.wastemanagement.data.local.database.payment.method

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface PaymentMethodDao : BaseDao<PaymentMethodEntity> {
  @Query("SELECT * FROM payment_method")
  suspend fun query(): List<PaymentMethodEntity>
}
