package net.techandgraphics.wastemanagement.ui.screen.company.client.history

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentPlanUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentWithMonthsCoveredUiModel

sealed interface CompanyClientHistoryState {
  object Loading : CompanyClientHistoryState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val plan: PaymentPlanUiModel,
    val payments: List<PaymentWithMonthsCoveredUiModel> = listOf(),
  ) : CompanyClientHistoryState
}
