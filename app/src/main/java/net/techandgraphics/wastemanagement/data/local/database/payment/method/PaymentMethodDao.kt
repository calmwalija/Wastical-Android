package net.techandgraphics.wastemanagement.data.local.database.payment.method

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface PaymentMethodDao : BaseDao<PaymentMethodEntity> {
  @Query("SELECT * FROM payment_method")
  abstract fun query(): Flow<List<PaymentMethodEntity>>
}
