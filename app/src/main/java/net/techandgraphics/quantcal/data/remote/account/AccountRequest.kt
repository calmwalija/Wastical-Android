package net.techandgraphics.quantcal.data.remote.account

import com.google.gson.annotations.SerializedName
import net.techandgraphics.quantcal.data.local.database.AccountRole
import net.techandgraphics.quantcal.data.local.database.account.AccountTitle
import java.util.UUID

data class AccountRequest(
  val uuid: String = UUID.randomUUID().toString(),
  val title: AccountTitle,
  val firstname: String,
  val lastname: String,
  val contacts: List<String>,
  val email: String? = null,
  val status: String,
  val role: AccountRole = AccountRole.Client,
  @SerializedName("http_operation") val httpOperation: String = HttpOperation.Post.name,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("leaving_reason") val leavingReason: String? = null,
  @SerializedName("leaving_timestamp") val leavingTimestamp: Long? = null,
  @SerializedName("company_location_id") val companyLocationId: Long,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
  @SerializedName("created_at") val createdAt: Long = 0,
  @SerializedName("updated_at") val updateAt: Long = 0,
)

enum class HttpOperation { Post, Put, Demographic }
