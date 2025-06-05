package net.techandgraphics.wastemanagement.data.local.database.account

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wastemanagement.data.local.database.demographic.street.StreetEntity

data class AccountStreetEntity(
  @Embedded val account: AccountEntity,
  @Relation(
    entity = StreetEntity::class,
    parentColumn = "street_id",
    entityColumn = "id",
  ) val street: StreetEntity,
)
