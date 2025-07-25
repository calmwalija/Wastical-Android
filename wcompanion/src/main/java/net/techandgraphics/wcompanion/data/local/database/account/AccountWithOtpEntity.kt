package net.techandgraphics.wcompanion.data.local.database.account

import androidx.room.Embedded
import androidx.room.Relation
import net.techandgraphics.wcompanion.data.local.database.otp.OtpEntity
import net.techandgraphics.wcompanion.domain.model.AccountWithOtpUiModel

data class AccountWithOtpEntity(
  @Embedded val otp: OtpEntity,
  @Relation(
    entity = AccountEntity::class,
    parentColumn = "account_id",
    entityColumn = "id",
  )
  val account: AccountEntity,
) {
  fun toAccountWithOtpUiModel() =
    AccountWithOtpUiModel(
      account = account.toAccountUiModel(),
      otp = otp.toOtpUiModel(),
    )
}
