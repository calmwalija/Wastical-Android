package net.techandgraphics.wastemanagement.data.local.database.account.session

import androidx.room.withTransaction
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toAccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyBinCollectionEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyLocationRequest
import net.techandgraphics.wastemanagement.data.local.database.toDemographicAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import javax.inject.Inject

class AccountSessionRepositoryImpl @Inject constructor(
  private val database: AppDatabase,
  private val sessionService: AccountApi,
) : AccountSessionRepository {

  override suspend fun fetchSession() {
    runCatching { sessionService.get(ACCOUNT_ID) }.onFailure { println(mapApiError(it)) }
      .onSuccess { accountSession ->
        try {
          with(database) {
            withTransaction {
              accountSession.run {
                paymentGateways?.map { it.toPaymentGatewayEntity() }?.also {
                  paymentGatewayDao.insert(it)
                }
                demographicDistricts?.map { it.toDemographicDistrictEntity() }
                  ?.also { demographicDistrictDao.insert(it) }
                demographicAreas?.map { it.toDemographicAreaEntity() }
                  ?.also { demographicAreaDao.insert(it) }
                demographicStreets?.map { it.toDemographicStreetEntity() }
                  ?.also { demographicStreetDao.insert(it) }
                companies?.map { it.toCompanyEntity() }?.also { companyDao.insert(it) }

                companyContacts?.map { it.toCompanyContactEntity() }
                  ?.also { companyContactDao.insert(it) }

                companyLocations?.map { it.toCompanyLocationRequest() }
                  ?.also { companyLocationDao.insert(it) }

                companyBinCollections?.map { it.toCompanyBinCollectionEntity() }
                  ?.also { companyBinCollectionDao.insert(it) }

                accounts?.map { it.toAccountEntity() }
                  ?.forEach { account -> accountDao.insert(account) }

                accountContacts?.map { it.toAccountContactEntity() }
                  ?.also { accountContactDao.insert(it) }
                paymentPlans?.map { it.toPaymentPlanEntity() }?.also { paymentPlanDao.insert(it) }
                accountPaymentPlans?.map { it.toAccountPaymentPlanEntity() }
                  ?.also { accountPaymentPlanDao.insert(it) }
                paymentCollectionDays?.map { it.toPaymentCollectionDayEntity() }
                  ?.also { paymentDayDao.insert(it) }

                paymentMethods?.map { it.toPaymentMethodEntity() }
                  ?.map { it.copy(isSelected = (it.account == "In Person")) }
                  ?.also { paymentMethodDao.insert(it) }

                payments?.map { it.toPaymentEntity() }?.also { paymentDao.insert(it) }

                paymentMonthsCovered?.map { it.toPaymentMonthCoveredEntity() }
                  ?.also { paymentMonthCoveredDao.insert(it) }
              }
            }
          }
        } catch (e: Exception) {
          println(e)
        }
      }
  }
}
