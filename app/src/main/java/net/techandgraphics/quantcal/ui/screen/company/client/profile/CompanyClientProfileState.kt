package net.techandgraphics.quantcal.ui.screen.company.client.profile

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel
import net.techandgraphics.quantcal.domain.model.company.CompanyUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.quantcal.domain.model.payment.PaymentUiModel
import net.techandgraphics.quantcal.domain.model.relations.CompanyLocationWithDemographicUiModel

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
