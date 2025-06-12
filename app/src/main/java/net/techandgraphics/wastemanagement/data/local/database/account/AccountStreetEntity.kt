package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.DemographicStreetEntity

data class AccountStreetEntity(
  @Embedded val account: AccountEntity,
  @Relation(
    entity = DemographicStreetEntity::class,
    parentColumn = "street_id",
    entityColumn = "id",
  ) val street: DemographicStreetEntity,
)
