package net.techandgraphics.wastical.ui.screen.auth.phone.load

import net.techandgraphics.wastical.domain.model.account.AccountUiModel

sealed interface LoadState {
  data object Loading : LoadState
  data class Success(val account: AccountUiModel? = null) : LoadState
}
