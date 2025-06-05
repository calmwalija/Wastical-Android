package net.techandgraphics.wastemanagement.domain.model.account

import net.techandgraphics.wastemanagement.domain.model.demographic.StreetUiModel

data class AccountStreetUiModel(
  val account: AccountUiModel,
  val street: StreetUiModel,
)
