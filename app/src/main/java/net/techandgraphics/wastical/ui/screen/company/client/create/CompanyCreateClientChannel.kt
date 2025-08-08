package net.techandgraphics.wastical.ui.screen.company.client.create

import net.techandgraphics.wastical.data.remote.ApiResult
import net.techandgraphics.wastical.domain.model.account.AccountInfoUiModel

sealed interface CompanyCreateClientChannel {
  data class Success(val id: Long) : CompanyCreateClientChannel
  data class Error(val error: ApiResult.Error) : CompanyCreateClientChannel

  sealed interface Input : CompanyCreateClientChannel {
    sealed interface Unique : Input {
      data class Conflict(val accounts: List<AccountInfoUiModel>) : Unique
      data object Ok : Unique
    }
  }
}
