package net.techandgraphics.quantcal.ui.screen.client.home

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface ClientHomeChannel {
  sealed interface Fetch : ClientHomeChannel {
    data object Fetching : Fetch
    data object Success : Fetch
    data class Error(val error: ApiResult.Error) : Fetch
  }

  sealed interface Goto : ClientHomeChannel {
    data object Login : Goto
    data object Reload : Goto
  }
}
