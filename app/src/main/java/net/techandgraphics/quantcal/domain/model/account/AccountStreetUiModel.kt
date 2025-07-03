package net.techandgraphics.quantcal.domain.model.account

import net.techandgraphics.quantcal.domain.model.demographic.DemographicStreetUiModel

data class AccountStreetUiModel(
  val account: AccountUiModel,
  val street: DemographicStreetUiModel,
)
