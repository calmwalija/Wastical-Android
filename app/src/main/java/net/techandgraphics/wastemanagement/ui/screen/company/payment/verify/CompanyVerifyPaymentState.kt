package net.techandgraphics.wastemanagement.ui.screen.company.payment.verify

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class CompanyVerifyPaymentState(
  val payments: List<PaymentAccountUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
)
