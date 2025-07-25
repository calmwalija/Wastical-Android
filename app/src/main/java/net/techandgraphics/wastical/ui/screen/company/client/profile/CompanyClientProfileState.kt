package net.techandgraphics.wastical.ui.screen.company.client.profile

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastical.domain.model.relations.CompanyLocationWithDemographicUiModel

sealed interface CompanyClientProfileState {
  object Loading : CompanyClientProfileState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val demographic: CompanyLocationWithDemographicUiModel,
    val payments: List<PaymentUiModel> = listOf(),
    val pending: List<PaymentRequestUiModel> = listOf(),
  ) : CompanyClientProfileState
}
