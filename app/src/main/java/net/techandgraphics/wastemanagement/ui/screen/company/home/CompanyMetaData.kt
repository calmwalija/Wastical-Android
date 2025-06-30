package net.techandgraphics.wastemanagement.ui.screen.company.home

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.remote.ServerResponse
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.month.covered.PaymentMonthCoveredResponse
import java.time.ZonedDateTime

data class CompanyMetaData(
  val ofType: MetaType = MetaType.Request,
  val currentTimeMillis: Long = System.currentTimeMillis(),
  val serverResponse: ServerResponse? = null,
  val payments: List<PaymentResponse> = listOf(),
  @SerializedName("payment_months_covered") val monthCovered: List<PaymentMonthCoveredResponse> = listOf(),
  val plans: List<AccountPaymentPlanResponse> = listOf(),
  val hashable: String = "",
) {
  fun toHash(): String =
    Gson().toJson(currentTimeMillis.times(0.34023832).plus(ZonedDateTime.now().toEpochSecond()))
}

enum class MetaType { Request, Response }
