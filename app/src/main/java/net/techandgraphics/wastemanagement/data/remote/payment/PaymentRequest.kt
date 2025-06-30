package net.techandgraphics.wastemanagement.data.remote.payment

import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime

data class PaymentRequest(
  val months: Int,
  @SerializedName("screenshot_text") val screenshotText: String,
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("executed_by_id") val executedById: Long,
  @SerializedName("payment_status") val status: PaymentStatus = PaymentStatus.Failed,
  @SerializedName("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
)
