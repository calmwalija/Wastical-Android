package net.techandgraphics.wastical.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.PaymentEntity

data class PaymentWithAccountEntity(
  @Embedded val payment: PaymentEntity,
  @Relation(
    entity = AccountEntity::class,
    parentColumn = "account_id",
    entityColumn = "id",
  ) val account: AccountEntity,
)
