package net.techandgraphics.quantcal.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.quantcal.data.local.database.company.location.CompanyLocationEntity
import net.techandgraphics.quantcal.data.local.database.demographic.area.DemographicAreaEntity
import net.techandgraphics.quantcal.data.local.database.demographic.street.DemographicStreetEntity

data class CompanyLocationWithDemographicEntity(
  @Embedded
  val location: CompanyLocationEntity,
  @Relation(
    entity = DemographicAreaEntity::class,
    parentColumn = "demographic_area_id",
    entityColumn = "id",
  )
  val demographicArea: DemographicAreaEntity,
  @Relation(
    entity = DemographicStreetEntity::class,
    parentColumn = "demographic_street_id",
    entityColumn = "id",
  )
  val demographicStreet: DemographicStreetEntity,
)
