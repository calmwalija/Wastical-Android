package net.techandgraphics.wastical.data.remote.payment

import com.google.gson.annotations.SerializedName
import java.time.ZonedDateTime

data class PaymentRequest(
  val months: Int,
  @SerializedName("screenshot_text") val screenshotText: String = "",
  @SerializedName("payment_method_id") val paymentMethodId: Long,
  @SerializedName("account_id") val accountId: Long,
  @SerializedName("company_id") val companyId: Long,
  @SerializedName("proof_ext") val proofExt: String? = null,
  @SerializedName("payment_reference") val paymentReference: String,
  @SerializedName("executed_by_id") val executedById: Long,
  @SerializedName("http_operation") val httpOperation: String,
  @SerializedName("payment_status") val status: PaymentStatus = PaymentStatus.Failed,
  @SerializedName("updated_at") val updateAt: Long = ZonedDateTime.now().toEpochSecond(),
  @SerializedName("created_at") val createdAt: Long = ZonedDateTime.now().toEpochSecond(),
)
