package net.techandgraphics.quantcal.data.remote.account.plan

import com.google.gson.annotations.SerializedName

data class AccountPaymentPlanRequest(
  @SerializedName("account_uuid") val accountUuid: String,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("payment_plan_id") val paymentPlanId: Long,
)
