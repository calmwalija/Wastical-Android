package net.techandgraphics.wastemanagement.ui.screen.company.home

import net.techandgraphics.wastemanagement.domain.model.payment.PaymentAccountUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class CompanyHomeState(
  val payments: List<PaymentAccountUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
)
