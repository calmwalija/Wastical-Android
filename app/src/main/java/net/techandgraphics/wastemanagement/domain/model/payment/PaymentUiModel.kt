package net.techandgraphics.wastemanagement.domain.model.payment

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus

data class PaymentUiModel(
  val id: Long,
  val screenshotText: String,
  val transactionId: String,
  val paymentMethodId: Long,
  val accountId: Long,
  val status: PaymentStatus,
  val createdAt: Long,
  val updatedAt: Long,
  val companyId: Long,
  val executedById: Long,
)
