package net.techandgraphics.quantcal.ui.screen.company.client.profile

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface CompanyClientProfileChannel {

  sealed interface Revoke : CompanyClientProfileChannel {
    data object Success : Revoke
    data class Error(val error: ApiResult.Error) : Revoke
  }
}
