package net.techandgraphics.wastical.ui.screen.client.settings

import net.techandgraphics.wastical.domain.model.account.AccountContactUiModel
import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyContactUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel
import net.techandgraphics.wastical.domain.model.payment.PaymentPlanUiModel

sealed interface ClientSettingsState {
  data object Loading : ClientSettingsState
  data class Success(
    val account: AccountUiModel,
    val company: CompanyUiModel,
    val plan: PaymentPlanUiModel,
    val contacts: List<AccountContactUiModel>,
    val companyContacts: List<CompanyContactUiModel> = listOf(),
    val areaName: String = "",
    val streetName: String = "",
    val dynamicColor: Boolean = false,
    val darkTheme: Boolean = false,
    val reminderPayment: Boolean = true,
    val reminderBin: Boolean = true,
  ) : ClientSettingsState
}
