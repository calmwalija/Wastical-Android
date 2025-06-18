package net.techandgraphics.wastemanagement.ui.screen.company.client.pending

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.PaymentRequestWithAccountUiModel

sealed interface CompanyClientPendingPaymentState {
  data object Loading : CompanyClientPendingPaymentState

  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val pending: List<PaymentRequestWithAccountUiModel> = listOf(),
  ) : CompanyClientPendingPaymentState
}
