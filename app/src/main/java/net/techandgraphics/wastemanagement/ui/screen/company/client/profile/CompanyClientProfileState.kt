package net.techandgraphics.wastemanagement.ui.screen.company.client.profile

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.PaymentUiModel

sealed interface CompanyClientProfileState {
  object Loading : CompanyClientProfileState
  data class Success(
    val account: AccountUiModel,
    val payments: List<PaymentUiModel> = listOf(),
  ) : CompanyClientProfileState
}
