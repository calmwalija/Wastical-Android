package net.techandgraphics.wastical.data.local.database.payment.collection

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao

@Dao
interface PaymentCollectionDayDao : BaseDao<PaymentCollectionDayEntity> {
  @Query("SELECT * FROM payment_collection_day")
  suspend fun query(): List<PaymentCollectionDayEntity>

  @Query("SELECT * FROM payment_collection_day")
  fun flow(): Flow<List<PaymentCollectionDayEntity>>
}
