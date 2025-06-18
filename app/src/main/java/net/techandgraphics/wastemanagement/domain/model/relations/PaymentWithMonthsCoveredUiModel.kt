package net.techandgraphics.wastemanagement.domain.model.relations

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentMonthCoveredUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

data class PaymentWithMonthsCoveredUiModel(
  val payment: PaymentUiModel,
  val covered: List<PaymentMonthCoveredUiModel>,
)
