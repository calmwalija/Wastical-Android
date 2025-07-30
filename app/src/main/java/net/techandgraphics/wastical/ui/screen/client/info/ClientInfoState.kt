package net.techandgraphics.wastical.ui.screen.client.info

import net.techandgraphics.wastical.domain.model.account.AccountUiModel
import net.techandgraphics.wastical.domain.model.company.CompanyUiModel

sealed interface ClientInfoState {
  data object Loading : ClientInfoState
  data class Success(
    val company: CompanyUiModel,
    val account: AccountUiModel,
    val newAccount: AccountUiModel,
  ) : ClientInfoState
}
