package net.techandgraphics.wastemanagement.data.remote.payment.pay

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentRequest
import java.io.File

interface PaymentRepository {

  suspend fun onPay(file: File, request: PaymentRequest): PaymentResponse
  suspend fun onPut(id: Long, request: PaymentRequest): List<PaymentResponse>
}
