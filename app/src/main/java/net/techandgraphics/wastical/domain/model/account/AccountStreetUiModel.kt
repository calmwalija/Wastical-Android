package net.techandgraphics.wastical.domain.model.account

import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel

data class AccountStreetUiModel(
  val account: AccountUiModel,
  val street: DemographicStreetUiModel,
)
