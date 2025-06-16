package net.techandgraphics.wastemanagement.data.local.database.payment.pay.request

import androidx.room.Dao
import androidx.room.Query
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao interface PaymentRequestDao : BaseDao<PaymentRequestEntity> {
  @Query("SELECT * FROM payment_request")
  suspend fun query(): List<PaymentRequestEntity>
}
