package net.techandgraphics.wastical.data.remote.account.plan

import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime

data class AccountPaymentPlanRequest(
  @SerializedName("account_uuid") val accountUuid: String,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long = ZonedDateTime.now().toEpochSecond(),
)
