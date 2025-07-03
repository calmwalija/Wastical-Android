package net.techandgraphics.quantcal.ui.screen.company.client.create

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface CompanyCreateClientChannel {
  data class Success(val id: Long) : CompanyCreateClientChannel
  data class Error(val error: ApiResult.Error) : CompanyCreateClientChannel

  sealed interface Input : CompanyCreateClientChannel {
    sealed interface Unique : Input {
      data object Conflict : Unique
      data object Ok : Unique
    }
  }
}
