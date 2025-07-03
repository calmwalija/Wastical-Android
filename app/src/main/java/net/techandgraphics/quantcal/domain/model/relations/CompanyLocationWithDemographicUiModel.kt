package net.techandgraphics.quantcal.domain.model.relations

import net.techandgraphics.quantcal.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.quantcal.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.quantcal.domain.model.payment.CompanyLocationUiModel

data class CompanyLocationWithDemographicUiModel(
  val location: CompanyLocationUiModel,
  val demographicArea: DemographicAreaUiModel,
  val demographicStreet: DemographicStreetUiModel,
)
