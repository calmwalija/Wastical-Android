package net.techandgraphics.wastical.ui.screen.company.client.pending

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.wastical.domain.model.relations.PaymentRequestWithAccountUiModel

sealed interface CompanyClientPendingPaymentState {
  data object Loading : CompanyClientPendingPaymentState

  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val pending: List<PaymentRequestWithAccountUiModel> = listOf(),
  ) : CompanyClientPendingPaymentState
}
