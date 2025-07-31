package net.techandgraphics.wastical.data.local.database.payment.collection

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastical.data.local.database.BaseDao
import net.techandgraphics.wastical.data.local.database.TimestampedDao

@Dao
interface PaymentCollectionDayDao : BaseDao<PaymentCollectionDayEntity>, TimestampedDao {

  @Query("SELECT updated_at FROM payment_collection_day ORDER BY updated_at DESC LIMIT 1")
  override suspend fun getLastUpdatedTimestamp(): Long

  @Query("SELECT * FROM payment_collection_day")
  suspend fun query(): List<PaymentCollectionDayEntity>

  @Query("SELECT * FROM payment_collection_day")
  fun flow(): Flow<List<PaymentCollectionDayEntity>>
}
