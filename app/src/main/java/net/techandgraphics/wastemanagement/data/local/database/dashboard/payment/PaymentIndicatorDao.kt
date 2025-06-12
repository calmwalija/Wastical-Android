package net.techandgraphics.wastemanagement.data.local.database.dashboard.payment

import androidx.room.Dao
import androidx.room.Query

@Dao
interface PaymentIndicatorDao {

  @Query(
    """
        SELECT
            date(created_at, 'unixepoch') AS paymentDate,
            COUNT(*) AS totalPayments,
            SUM(payment_plan_fee) AS totalAmount
        FROM payment
        GROUP BY paymentDate
        ORDER BY paymentDate
    """,
  )
  suspend fun getDailyPaymentSummary(): List<DailyPaymentSummary>
}

data class DailyPaymentSummary(
  val paymentDate: String,
  val totalPayments: Int,
  val totalAmount: Int,
)
