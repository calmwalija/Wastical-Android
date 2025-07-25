package net.techandgraphics.wastical.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wastical.data.local.database.account.AccountEntity
import net.techandgraphics.wastical.data.local.database.payment.pay.request.PaymentRequestEntity

data class PaymentRequestWithAccountEntity(
  @Embedded()
  val payment: PaymentRequestEntity,
  @Relation(
    entity = AccountEntity::class,
    parentColumn = "account_id",
    entityColumn = "id",
  )
  val account: AccountEntity,
  val fee: Int,
)
