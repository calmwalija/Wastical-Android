package net.techandgraphics.wastemanagement.domain.model.payment

import net.techandgraphics.wastemanagement.data.remote.payment.PaymentStatus

data class PaymentUiModel(
  val id: Long,
  val screenshotText: String,
  val numberOfMonths: Int,
  val transactionId: String,
  val paymentMethodId: Long,
  val accountId: Long,
  val status: PaymentStatus,
  val createdAt: Long,
  val updatedAt: Long?,

  val paymentPlanId: Long,
  val paymentPlanFee: Int,
  val paymentPlanPeriod: String,
  val paymentGatewayId: Long,
  val paymentGatewayName: String,
  val paymentGatewayType: String,
)
