package net.techandgraphics.wastical.ui.screen.company.client.info

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface CompanyClientInfoChannel {

  sealed interface Submit : CompanyClientInfoChannel {
    data object Success : Submit
    data class Error(val error: ApiResult.Error) : Submit
  }
}
