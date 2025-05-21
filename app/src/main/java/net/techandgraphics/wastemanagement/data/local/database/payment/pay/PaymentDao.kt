package net.techandgraphics.wastemanagement.data.local.database.payment.pay

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao
interface PaymentDao : BaseDao<PaymentEntity> {
  @Query("SELECT * FROM payment")
  fun query(): Flow<List<PaymentEntity>>

  @Query("SELECT id FROM payment ORDER BY id DESC LIMIT 1")
  suspend fun getLastId(): Long?
}
