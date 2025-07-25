package net.techandgraphics.wastical.ui.screen.company.client.profile

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface CompanyClientProfileChannel {

  sealed interface Revoke : CompanyClientProfileChannel {
    data object Success : Revoke
    data class Error(val error: ApiResult.Error) : Revoke
  }
}
