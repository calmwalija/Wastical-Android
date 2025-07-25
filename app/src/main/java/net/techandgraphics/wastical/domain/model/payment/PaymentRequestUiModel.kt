package net.techandgraphics.wastical.domain.model.payment

data class PaymentRequestUiModel(
  val id: Long,
  val months: Int,
  val screenshotText: String,
  val paymentMethodId: Long,
  val accountId: Long,
  val companyId: Long,
  val executedById: Long,
  val status: String,
  val createdAt: Long,
)
