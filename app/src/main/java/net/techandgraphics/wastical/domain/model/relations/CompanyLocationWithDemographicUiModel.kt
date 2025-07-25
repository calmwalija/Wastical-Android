package net.techandgraphics.wastical.domain.model.relations

import net.techandgraphics.wastical.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastical.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastical.domain.model.payment.CompanyLocationUiModel

data class CompanyLocationWithDemographicUiModel(
  val location: CompanyLocationUiModel,
  val demographicArea: DemographicAreaUiModel,
  val demographicStreet: DemographicStreetUiModel,
)
