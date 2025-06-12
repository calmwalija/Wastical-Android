package net.techandgraphics.wastemanagement.domain.model.account

import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicStreetUiModel

data class AccountStreetUiModel(
  val account: AccountUiModel,
  val street: DemographicStreetUiModel,
)
