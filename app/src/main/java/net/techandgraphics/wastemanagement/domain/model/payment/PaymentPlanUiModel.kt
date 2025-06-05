package net.techandgraphics.wastemanagement.domain.model.payment

import net.techandgraphics.wastemanagement.data.PaymentPeriod
import net.techandgraphics.wastemanagement.data.Status

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
