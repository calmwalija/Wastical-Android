package net.techandgraphics.quantcal.ui.screen.auth.phone.load

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel

sealed interface LoadState {
  data object Loading : LoadState
  data class Success(val account: AccountUiModel? = null) : LoadState
}
