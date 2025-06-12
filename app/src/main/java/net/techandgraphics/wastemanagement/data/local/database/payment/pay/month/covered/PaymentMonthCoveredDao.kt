package net.techandgraphics.wastemanagement.data.local.database.payment.pay.month.covered

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import net.techandgraphics.wastemanagement.data.local.database.BaseDao

@Dao interface PaymentMonthCoveredDao : BaseDao<PaymentMonthCoveredEntity> {

  @Query("SELECT * FROM payment_month_covered")
  fun flowOfPaymentMonthCovered(): Flow<List<PaymentMonthCoveredEntity>>
}
