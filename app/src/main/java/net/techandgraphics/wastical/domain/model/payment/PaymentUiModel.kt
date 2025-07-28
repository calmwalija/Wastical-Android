package net.techandgraphics.wastical.domain.model.payment

import net.techandgraphics.wastical.data.remote.payment.PaymentStatus

data class PaymentUiModel(
  val id: Long,
  val screenshotText: String,
  val transactionId: String,
  val paymentMethodId: Long,
  val paymentReference: String,
  val accountId: Long,
  val status: PaymentStatus,
  val createdAt: Long,
  val updatedAt: Long,
  val companyId: Long,
  val executedById: Long,
)
