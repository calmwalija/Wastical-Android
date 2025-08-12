package net.techandgraphics.wastical.ui.screen.company.client.info

import net.techandgraphics.wastical.data.remote.ApiResult
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel

sealed interface CompanyClientInfoChannel {

  sealed interface Submit : CompanyClientInfoChannel {
    data object Success : Submit
    data class Error(val error: ApiResult.Error) : Submit
  }

  sealed interface Input : CompanyClientInfoChannel {
    sealed interface Unique : Input {
      data class Conflict(val accounts: List<AccountInfoUiModel>) : Unique
      data object Ok : Unique
    }
  }
}
