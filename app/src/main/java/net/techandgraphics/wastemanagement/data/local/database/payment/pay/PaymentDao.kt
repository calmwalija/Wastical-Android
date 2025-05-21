package net.techandgraphics.wastemanagement.data.local.database.payment.pay

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus

@Dao interface PaymentDao : BaseDao<PaymentEntity> {
  @Query("SELECT * FROM payment ORDER BY id DESC")
  suspend fun query(): List<PaymentEntity>

  @Query("SELECT id FROM payment ORDER BY id DESC LIMIT 1")
  suspend fun getLastId(): Long?

  @Query("SELECT * FROM payment WHERE payment_status=:status")
  suspend fun queryRetry(status: String = PaymentStatus.Retry.name): List<PaymentEntity>
}
