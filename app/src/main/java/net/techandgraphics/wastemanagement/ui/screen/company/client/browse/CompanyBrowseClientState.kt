package net.techandgraphics.wastemanagement.ui.screen.company.client.browse

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel
import net.techandgraphics.wastemanagement.ui.activity.main.activity.main.MainActivityState

data class CompanyBrowseClientState(
  val accounts: List<AccountUiModel> = listOf(),
  val state: MainActivityState = MainActivityState(),
  val query: String = "",
)
