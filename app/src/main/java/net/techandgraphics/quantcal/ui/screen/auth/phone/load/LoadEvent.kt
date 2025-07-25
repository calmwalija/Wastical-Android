package net.techandgraphics.quantcal.ui.screen.auth.phone.load

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel

sealed interface LoadEvent {
  data object Load : LoadEvent
  data object Logout : LoadEvent
  data object NoAccount : LoadEvent
  data class Success(val account: AccountUiModel) : LoadEvent
}
