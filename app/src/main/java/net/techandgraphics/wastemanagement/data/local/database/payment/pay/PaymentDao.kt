package net.techandgraphics.wastemanagement.data.local.database.payment.pay

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus

@Dao interface PaymentDao : BaseDao<PaymentEntity> {

  @Query("SELECT * FROM payment WHERE payment_status !=:status ORDER BY id DESC LIMIT 3")
  suspend fun payments(status: String = PaymentStatus.Approved.name): List<PaymentEntity>

  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id DESC LIMIT 4")
  suspend fun invoices(status: String = PaymentStatus.Approved.name): List<PaymentEntity>

  @Query("SELECT id FROM payment ORDER BY id DESC LIMIT 1")
  suspend fun getLastId(): Long?

  @Query("SELECT * FROM payment WHERE payment_status=:status")
  suspend fun queryRetry(status: String = PaymentStatus.Failed.name): List<PaymentEntity>
}
