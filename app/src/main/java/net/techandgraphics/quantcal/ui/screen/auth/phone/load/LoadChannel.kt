package net.techandgraphics.quantcal.ui.screen.auth.phone.load

import net.techandgraphics.quantcal.data.remote.ApiResult

sealed interface LoadChannel {
  data object Success : LoadChannel
  data object NoAccount : LoadChannel
  data class Error(val error: ApiResult.Error) : LoadChannel
}
