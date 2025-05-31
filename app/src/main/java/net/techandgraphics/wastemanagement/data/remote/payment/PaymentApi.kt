package net.techandgraphics.wastemanagement.data.remote.payment

import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import java.io.File

interface PaymentApi {
  suspend fun pay(file: File, request: PaymentRequest): PaymentResponse
  suspend fun put(id: Long, request: PaymentRequest): List<PaymentResponse>
  suspend fun fetchLatest(accountId: Long, epochSecond: Long): List<PaymentResponse>
}
