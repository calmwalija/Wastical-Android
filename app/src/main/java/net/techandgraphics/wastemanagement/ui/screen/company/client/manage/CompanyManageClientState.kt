package net.techandgraphics.wastemanagement.ui.screen.company.client.manage

import net.techandgraphics.wastemanagement.domain.model.account.AccountUiModel

data class CompanyManageClientState(
  val accounts: List<AccountUiModel> = listOf(),
)
