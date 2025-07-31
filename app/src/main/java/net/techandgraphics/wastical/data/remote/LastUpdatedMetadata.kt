package net.techandgraphics.wastical.data.remote

import com.google.gson.annotations.SerializedName
import net.techandgraphics.wastical.data.local.database.AppDatabase

data class LastUpdatedMetadata(
  @SerializedName("account") val accounts: Long,
  @SerializedName("account_contacts") val accountContacts: Long,
  @SerializedName("companies") val companies: Long,
  @SerializedName("company_contacts") val companyContacts: Long,
  @SerializedName("payment_plans") val paymentPlans: Long,
  @SerializedName("payments") val payments: Long,
  @SerializedName("payment_methods") val paymentMethods: Long,
  @SerializedName("payment_gateways") val paymentGateways: Long,
  @SerializedName("company_bin_collection") val companyBinCollections: Long,
  @SerializedName("payment_collection_days") val paymentCollectionDays: Long,
  @SerializedName("account_payment_plans") val accountPaymentPlans: Long,
  @SerializedName("payment_months_covered") val paymentMonthsCovered: Long,
  @SerializedName("demographic_streets") val demographicStreets: Long,
  @SerializedName("demographic_areas") val demographicAreas: Long,
  @SerializedName("company_locations") val companyLocations: Long,
)

suspend fun getLastUpdatedTimestamp(database: AppDatabase, id: Long): LastUpdatedMetadata {
  val paymentPlans = database.paymentPlanDao.getLastUpdatedTimestamp()
  val payments = database.paymentDao.getLastUpdatedTimestamp()
  val paymentMethods = database.paymentMethodDao.getLastUpdatedTimestamp()
  val paymentGateways = database.paymentGatewayDao.getLastUpdatedTimestamp()
  val companyBinCollections = database.companyBinCollectionDao.getLastUpdatedTimestamp()
  val accountPaymentPlans = database.accountPaymentPlanDao.getLastUpdatedTimestamp()
  val paymentMonthsCovered = database.paymentMonthCoveredDao.getLastUpdatedTimestamp()
  val companyLocations = database.companyLocationDao.getLastUpdatedTimestamp()
  val accounts = database.accountDao.getLastUpdatedTimestampById(id)
  val accountContacts = database.accountContactDao.getLastUpdatedTimestamp()
  val companies = database.companyDao.getLastUpdatedTimestamp()
  val companyContacts = database.companyContactDao.getLastUpdatedTimestamp()
  val paymentCollectionDays = database.paymentCollectionDayDao.getLastUpdatedTimestamp()
  val demographicStreets = database.demographicStreetDao.getLastUpdatedTimestamp()
  val demographicAreas = database.demographicAreaDao.getLastUpdatedTimestamp()
  return LastUpdatedMetadata(
    accounts = accounts,
    accountContacts = accountContacts,
    companies = companies,
    companyContacts = companyContacts,
    paymentPlans = paymentPlans,
    payments = payments,
    paymentMethods = paymentMethods,
    paymentGateways = paymentGateways,
    companyBinCollections = companyBinCollections,
    paymentCollectionDays = paymentCollectionDays,
    accountPaymentPlans = accountPaymentPlans,
    paymentMonthsCovered = paymentMonthsCovered,
    demographicStreets = demographicStreets,
    demographicAreas = demographicAreas,
    companyLocations = companyLocations,
  )
}
