package net.techandgraphics.wastemanagement.ui.screen.company.client.list

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class CompanyListClientState(
  val accounts: List<AccountUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
)
