package net.techandgraphics.wastemanagement.domain.model.payment

data class PaymentMonthCoveredUiModel(
  val id: Long,
  val month: Int,
  val year: Int,
  val paymentId: Long,
  val accountId: Long,
  val createdAt: Long,
  val updatedAt: Long,
)
