package net.techandgraphics.wastemanagement.data.remote.account.session

import net.techandgraphics.wastemanagement.data.remote.account.AccountResponse
import net.techandgraphics.wastemanagement.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyResponse
import net.techandgraphics.wastemanagement.data.remote.company.trash.collection.schedule.TrashCollectionScheduleResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.AreaResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DistrictResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.StreetResponse
import net.techandgraphics.wastemanagement.data.remote.payment.collection.PaymentCollectionDayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.gateway.PaymentGatewayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.plan.PaymentPlanResponse

data class AccountSessionResponse(
  val account: List<AccountResponse>,
  val accountContacts: List<AccountContactResponse>,
  val companies: List<CompanyResponse>,
  val companyContacts: List<CompanyContactResponse>,
  val plans: List<PaymentPlanResponse>,
  val payments: List<PaymentResponse>,
  val methods: List<PaymentMethodResponse>,
  val gateways: List<PaymentGatewayResponse>,
  val streets: List<StreetResponse>,
  val areas: List<AreaResponse>,
  val districts: List<DistrictResponse>,
  val trashSchedules: List<TrashCollectionScheduleResponse>,
  val paymentDays: List<PaymentCollectionDayResponse>,
  val accountPaymentPlans: List<AccountPaymentPlanResponse> = listOf(),
)
