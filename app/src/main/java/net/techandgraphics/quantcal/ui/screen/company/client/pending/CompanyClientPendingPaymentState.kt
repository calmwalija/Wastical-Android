package net.techandgraphics.quantcal.ui.screen.company.client.pending

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel
import net.techandgraphics.quantcal.domain.model.relations.PaymentRequestWithAccountUiModel

sealed interface CompanyClientPendingPaymentState {
  data object Loading : CompanyClientPendingPaymentState

  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val pending: List<PaymentRequestWithAccountUiModel> = listOf(),
  ) : CompanyClientPendingPaymentState
}
