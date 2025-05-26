package net.techandgraphics.wastemanagement.data.remote.account

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle

data class AccountRequest(
  val title: AccountTitle,
  val firstname: String?,
  val lastname: String,
  val contacts: List<String>,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("trash_collection_schedule_id") val tCSId: Long,
)
