package net.techandgraphics.wastemanagement.data.local.database.payment.pay

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus.Approved
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus.Failed

@Dao interface PaymentDao : BaseDao<PaymentEntity> {

  @Query("SELECT * FROM payment WHERE payment_status !=:status ORDER BY id DESC LIMIT 4")
  fun flowOfPayment(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id DESC LIMIT 3")
  fun flowOfInvoice(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT * FROM payment WHERE payment_status =:status ORDER BY id")
  fun flowOfAllInvoices(status: String = Approved.name): Flow<List<PaymentEntity>>

  @Query("SELECT id FROM payment ORDER BY id DESC LIMIT 1")
  suspend fun getLastId(): Long?

  @Query("SELECT * FROM payment WHERE payment_status=:status")
  suspend fun queryRetry(status: String = Failed.name): List<PaymentEntity>
}
