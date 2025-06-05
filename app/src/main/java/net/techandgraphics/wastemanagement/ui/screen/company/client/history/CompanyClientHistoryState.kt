package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

sealed interface CompanyClientHistoryState {
  object Loading : CompanyClientHistoryState
  data class Success(
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val payments: List<PaymentUiModel> = listOf(),
    val state: MainActivityState = MainActivityState(),
  ) : CompanyClientHistoryState
}
