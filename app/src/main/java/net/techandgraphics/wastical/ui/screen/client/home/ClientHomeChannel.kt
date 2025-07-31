package net.techandgraphics.wastical.ui.screen.client.home

import net.techandgraphics.wastical.data.remote.ApiResult

sealed interface ClientHomeChannel {

  sealed interface Fetch : ClientHomeChannel {
    data object Success : Fetch
    data class Error(val error: ApiResult.Error) : Fetch
  }

  sealed interface Goto : ClientHomeChannel {
    data object Login : Goto
    data object Reload : Goto
  }
}
