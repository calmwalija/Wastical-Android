package net.techandgraphics.quantcal.ui.screen.company.client.info

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface CompanyClientInfoChannel {

  sealed interface Submit : CompanyClientInfoChannel {
    data object Success : Submit
    data class Error(val error: ApiResult.Error) : Submit
  }
}
