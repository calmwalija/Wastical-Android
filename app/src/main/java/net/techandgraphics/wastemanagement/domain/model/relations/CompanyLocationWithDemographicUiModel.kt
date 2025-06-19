package net.techandgraphics.wastemanagement.domain.model.relations

import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicAreaUiModel
import net.techandgraphics.wastemanagement.domain.model.demographic.DemographicStreetUiModel
import net.techandgraphics.wastemanagement.domain.model.payment.CompanyLocationUiModel

data class CompanyLocationWithDemographicUiModel(
  val location: CompanyLocationUiModel,
  val demographicArea: DemographicAreaUiModel,
  val demographicStreet: DemographicStreetUiModel,
)
