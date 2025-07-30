package net.techandgraphics.wastical.ui.screen.client.info

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface ClientInfoChannel {
  sealed interface Submit : ClientInfoChannel {
    data object Success : Submit
    data class Error(val error: ApiResult.Error) : Submit
  }
}
