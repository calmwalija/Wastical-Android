package net.techandgraphics.wastemanagement.data.remote.payment.collection

import com.google.gson.annotations.SerializedName

data class PaymentCollectionDayResponse(
  val id: Long,
  val day: Int,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("created_at") val createdAt: Long,
  @SerializedName("updated_at") val updatedAt: Long,
)
