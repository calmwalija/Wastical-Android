package net.techandgraphics.quantcal.data.local.database.account.session

import androidx.room.withTransaction
import net.techandgraphics.quantcal.data.local.database.AppDatabase
import net.techandgraphics.quantcal.data.local.database.toAccountContactEntity
import net.techandgraphics.quantcal.data.local.database.toAccountEntity
import net.techandgraphics.quantcal.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.quantcal.data.local.database.toCompanyBinCollectionEntity
import net.techandgraphics.quantcal.data.local.database.toCompanyContactEntity
import net.techandgraphics.quantcal.data.local.database.toCompanyEntity
import net.techandgraphics.quantcal.data.local.database.toCompanyLocationRequest
import net.techandgraphics.quantcal.data.local.database.toDemographicAreaEntity
import net.techandgraphics.quantcal.data.local.database.toDemographicDistrictEntity
import net.techandgraphics.quantcal.data.local.database.toDemographicStreetEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentCollectionDayEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentGatewayEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMethodEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.quantcal.data.local.database.toPaymentPlanEntity
import net.techandgraphics.quantcal.data.remote.ServerResponse
import net.techandgraphics.quantcal.data.remote.account.ACCOUNT_ID
import net.techandgraphics.quantcal.data.remote.account.AccountApi
import net.techandgraphics.quantcal.data.remote.mapApiError
import javax.inject.Inject

class AccountSessionRepositoryImpl @Inject constructor(
  private val database: AppDatabase,
  private val sessionService: AccountApi,
) : AccountSessionRepository {

  override suspend fun fetch(): ServerResponse = sessionService.get(ACCOUNT_ID)

  override suspend fun fetchSession() {
    runCatching { sessionService.get(ACCOUNT_ID) }.onFailure { println(mapApiError(it)) }
      .onSuccess { accountSession ->
        runCatching {
          database.withTransaction {
            purseData(accountSession) { _, _ -> }
          }
        }
      }
      .onFailure { println(it) }
  }

  override suspend fun purseData(data: ServerResponse, onProgress: suspend (Int, Int) -> Unit) {
    with(database) {
      val totalItemCount = data.run {
        (accounts?.size ?: 0)
          .plus(accountContacts?.size ?: 0)
          .plus(companies?.size ?: 0)
          .plus(companyContacts?.size ?: 0)
          .plus(paymentPlans?.size ?: 0)
          .plus(payments?.size ?: 0)
          .plus(paymentMethods?.size ?: 0)
          .plus(paymentGateways?.size ?: 0)
          .plus(companyBinCollections?.size ?: 0)
          .plus(paymentCollectionDays?.size ?: 0)
          .plus(accountPaymentPlans?.size ?: 0)
          .plus(paymentMonthsCovered?.size ?: 0)
          .plus(demographicStreets?.size ?: 0)
          .plus(demographicAreas?.size ?: 0)
          .plus(demographicDistricts?.size ?: 0)
          .plus(companyLocations?.size ?: 0)
      }

      data.run {
        paymentGateways?.map { it.toPaymentGatewayEntity() }?.also {
          paymentGatewayDao.insert(it)
          onProgress(totalItemCount, it.size)
        }
        demographicDistricts?.map { it.toDemographicDistrictEntity() }
          ?.also {
            demographicDistrictDao.insert(it)
            onProgress(totalItemCount, it.size)
          }
        demographicAreas?.map { it.toDemographicAreaEntity() }
          ?.also {
            demographicAreaDao.insert(it)
            onProgress(totalItemCount, it.size)
          }
        demographicStreets?.map { it.toDemographicStreetEntity() }
          ?.also {
            demographicStreetDao.insert(it)
            onProgress(totalItemCount, it.size)
          }
        companies?.map { it.toCompanyEntity() }?.also {
          companyDao.insert(it)
          onProgress(totalItemCount, it.size)
        }

        companyContacts?.map { it.toCompanyContactEntity() }
          ?.also {
            companyContactDao.insert(it)
            onProgress(totalItemCount, it.size)
          }

        companyLocations?.map { it.toCompanyLocationRequest() }
          ?.also {
            companyLocationDao.insert(it)
            onProgress(totalItemCount, it.size)
          }

        companyBinCollections?.map { it.toCompanyBinCollectionEntity() }
          ?.also {
            companyBinCollectionDao.insert(it)
            onProgress(totalItemCount, it.size)
          }

        accounts?.map { it.toAccountEntity() }
          ?.also { account ->
            accountDao.insert(account)
            onProgress(totalItemCount, account.size)
          }

        accountContacts?.map { it.toAccountContactEntity() }
          ?.also {
            accountContactDao.insert(it)
            onProgress(totalItemCount, it.size)
          }
        paymentPlans?.map { it.toPaymentPlanEntity() }?.also {
          paymentPlanDao.insert(it)
          onProgress(totalItemCount, it.size)
        }
        accountPaymentPlans?.map { it.toAccountPaymentPlanEntity() }
          ?.also {
            accountPaymentPlanDao.insert(it)
            onProgress(totalItemCount, it.size)
          }
        paymentCollectionDays?.map { it.toPaymentCollectionDayEntity() }
          ?.also {
            paymentDayDao.insert(it)
            onProgress(totalItemCount, it.size)
          }

        paymentMethods?.map { it.toPaymentMethodEntity() }
          ?.map { it.copy(isSelected = (it.account == "In Person")) }
          ?.also {
            paymentMethodDao.insert(it)
            onProgress(totalItemCount, it.size)
          }

        payments?.map { it.toPaymentEntity() }?.also {
          paymentDao.insert(it)
          onProgress(totalItemCount, it.size)
        }

        paymentMonthsCovered?.map { it.toPaymentMonthCoveredEntity() }
          ?.also {
            paymentMonthCoveredDao.insert(it)
            onProgress(totalItemCount, it.size)
          }
      }
    }
  }
}
