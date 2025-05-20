package net.techandgraphics.wastemanagement.data.remote.payment.plan

import com.google.gson.annotations.SerializedName

data class PaymentPlanResponse(
  val id: Long,
  val fee: Int,
  val name: String,
  val period: String,
  val status: String,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long?,
)
