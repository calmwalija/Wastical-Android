package net.techandgraphics.quantcal.ui.activity

import net.techandgraphics.quantcal.domain.model.account.AccountUiModel

data class MainActivityState(
  val isLoading: Boolean = true,
  val account: AccountUiModel? = null,
  val holding: Boolean = true,
)
