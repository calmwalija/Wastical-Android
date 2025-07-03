package net.techandgraphics.quantcal.domain.model.payment

import net.techandgraphics.quantcal.data.PaymentPeriod
import net.techandgraphics.quantcal.data.Status

data class PaymentPlanUiModel(
  val id: Long,
  val fee: Int,
  val name: String,
  val period: PaymentPeriod,
  val status: Status,
  val companyId: Long,
  val createdAt: Long,
  val updatedAt: Long,
  val active: Boolean = false,
)
