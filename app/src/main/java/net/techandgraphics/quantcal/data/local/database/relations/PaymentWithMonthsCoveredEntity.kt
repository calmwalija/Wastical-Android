package net.techandgraphics.quantcal.data.local.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.quantcal.data.local.database.account.AccountEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.PaymentEntity
import net.techandgraphics.quantcal.data.local.database.payment.pay.month.covered.PaymentMonthCoveredEntity

data class PaymentWithMonthsCoveredEntity(
  @Embedded
  val payment: PaymentEntity,
  @Relation(
    entity = PaymentMonthCoveredEntity::class,
    parentColumn = "id",
    entityColumn = "payment_id",
  )
  val covered: List<PaymentMonthCoveredEntity>,
  @Relation(
    entity = AccountEntity::class,
    parentColumn = "account_id",
    entityColumn = "id",
  )
  val account: AccountEntity,
)
