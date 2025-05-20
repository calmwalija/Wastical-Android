package net.techandgraphics.wastemanagement.data.remote.session

import net.techandgraphics.wastemanagement.data.remote.dto.AccountWithContacts
import net.techandgraphics.wastemanagement.data.remote.dto.CompanyWithContacts
import net.techandgraphics.wastemanagement.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.plan.PaymentPlanResponse

data class SessionResponse(
  val account: AccountWithContacts,
  val company: CompanyWithContacts,
  val plans: List<PaymentPlanResponse>,
  val methods: List<PaymentMethodResponse>,
  val payments: List<PaymentResponse>,
)
