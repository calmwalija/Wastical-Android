package net.techandgraphics.wastemanagement.data.remote.account

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.local.database.AccountRole
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import java.util.UUID

data class AccountRequest(
  val uuid: String = UUID.randomUUID().toString(),
  val title: AccountTitle,
  val firstname: String?,
  val lastname: String,
  val contacts: List<String>,
  val role: AccountRole = AccountRole.Client,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("trash_collection_schedule_id") val tCSId: Long,
)
