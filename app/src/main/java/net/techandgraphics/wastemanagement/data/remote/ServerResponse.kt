package net.techandgraphics.wastemanagement.data.remote

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastemanagement.data.remote.account.AccountResponse
import net.techandgraphics.wastemanagement.data.remote.account.contact.AccountContactResponse
import net.techandgraphics.wastemanagement.data.remote.account.plan.AccountPaymentPlanResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyContactResponse
import net.techandgraphics.wastemanagement.data.remote.company.CompanyResponse
import net.techandgraphics.wastemanagement.data.remote.company.bin.collection.CompanyBinCollectionResponse
import net.techandgraphics.wastemanagement.data.remote.company.location.CompanyLocationResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DemographicAreaResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DemographicDistrictResponse
import net.techandgraphics.wastemanagement.data.remote.demographic.DemographicStreetResponse
import net.techandgraphics.wastemanagement.data.remote.payment.collection.PaymentCollectionDayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.gateway.PaymentGatewayResponse
import net.techandgraphics.wastemanagement.data.remote.payment.method.PaymentMethodResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.PaymentResponse
import net.techandgraphics.wastemanagement.data.remote.payment.pay.month.covered.PaymentMonthCoveredResponse
import net.techandgraphics.wastemanagement.data.remote.payment.plan.PaymentPlanResponse

data class ServerResponse(
  @SerializedName("account") val accounts: List<AccountResponse>? = null,
  @SerializedName("account_contacts") val accountContacts: List<AccountContactResponse>? = null,
  @SerializedName("companies") val companies: List<CompanyResponse>? = null,
  @SerializedName("company_contacts") val companyContacts: List<CompanyContactResponse>? = null,
  @SerializedName("payment_plans") val paymentPlans: List<PaymentPlanResponse>? = null,
  @SerializedName("payments") val payments: List<PaymentResponse>? = null,
  @SerializedName("payment_methods") val paymentMethods: List<PaymentMethodResponse>? = null,
  @SerializedName("payment_gateways") val paymentGateways: List<PaymentGatewayResponse>? = null,
  @SerializedName("company_bin_collection") val companyBinCollections: List<CompanyBinCollectionResponse>? = null,
  @SerializedName("payment_collection_days") val paymentCollectionDays: List<PaymentCollectionDayResponse>? = null,
  @SerializedName("account_payment_plans") val accountPaymentPlans: List<AccountPaymentPlanResponse>? = null,
  @SerializedName("payment_months_covered") val paymentMonthsCovered: List<PaymentMonthCoveredResponse>? = null,
  @SerializedName("demographic_streets") val demographicStreets: List<DemographicStreetResponse>? = null,
  @SerializedName("demographic_areas") val demographicAreas: List<DemographicAreaResponse>? = null,
  @SerializedName("demographic_districts") val demographicDistricts: List<DemographicDistrictResponse>? = null,
  @SerializedName("company_locations") val companyLocations: List<CompanyLocationResponse>? = null,
)
