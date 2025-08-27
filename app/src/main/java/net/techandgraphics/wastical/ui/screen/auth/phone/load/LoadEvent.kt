package net.techandgraphics.wastical.ui.screen.auth.phone.load

import net.techandgraphics.wastical.domain.model.account.AccountUiModel

sealed interface LoadEvent {
  data object Load : LoadEvent
  data object Logout : LoadEvent
  data object NoAccount : LoadEvent
  data class NoToken(val contact: String) : LoadEvent
  data class Success(val account: AccountUiModel) : LoadEvent
}
