package net.techandgraphics.quantcal.domain.model.account

import net.techandgraphics.quantcal.data.local.database.AccountRole
import net.techandgraphics.quantcal.data.local.database.account.AccountTitle
import net.techandgraphics.quantcal.data.remote.account.HttpOperation
import java.time.ZonedDateTime
import java.util.UUID

data class AccountRequestUiModel(
  val uuid: String = UUID.randomUUID().toString(),
  val title: AccountTitle,
  val firstname: String,
  val lastname: String,
  val contact: String,
  val altContact: String,
  val email: String? = null,
  val role: String = AccountRole.Client.name,
  val httpOperation: String = HttpOperation.Create.name,
  val companyId: Long,
  val accountId: Long,
  val companyLocationId: Long,
  val paymentPlanId: Long,
  val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
  val id: Long = 0,
)
