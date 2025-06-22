package net.techandgraphics.wastemanagement.data.remote.account

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.local.database.AccountRole
import net.techandgraphics.wastemanagement.data.local.database.account.AccountTitle
import java.util.UUID

data class AccountRequest(
  val uuid: String = UUID.randomUUID().toString(),
  val title: AccountTitle,
  val firstname: String,
  val lastname: String,
  val contacts: List<String>,
  val email: String? = null,
  val role: AccountRole = AccountRole.Client,
  @SerializedName("http_operation") val httpOperation: String = HttpOperation.Create.name,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("company_location_id") val companyLocationId: Long,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
)

enum class HttpOperation { Create, Edit }
