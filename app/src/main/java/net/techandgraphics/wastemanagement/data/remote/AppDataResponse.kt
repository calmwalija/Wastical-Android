package net.techandgraphics.wastemanagement.data.remote

import net.techandgraphics.wastemanagement.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastemanagement.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.plan.PaymentPlanResponse

data class AppDataResponse(
  val company: CompanyContactResponse,
  val plans: List<PaymentPlanResponse>,
  val methods: List<PaymentMethodResponse>,
  val payments: List<PaymentResponse>,
)
