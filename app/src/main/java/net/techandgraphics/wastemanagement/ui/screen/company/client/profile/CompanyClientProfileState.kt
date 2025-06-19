package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.company.CompanyUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentRequestUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel
import net.techandgraphics.wastemanagement.domain.model.relations.CompanyLocationWithDemographicUiModel

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
