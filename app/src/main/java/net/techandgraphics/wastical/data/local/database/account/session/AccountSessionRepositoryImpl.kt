package net.techandgraphics.wastical.data.local.database.account.session

import android.accounts.AccountManager
import androidx.room.withTransaction
import com.google.gson.Gson
import net.techandgraphics.wastical.account.AuthenticatorHelper
import net.techandgraphics.wastical.data.local.database.AppDatabase
import net.techandgraphics.wastical.data.local.database.toAccountContactEntity
import net.techandgraphics.wastical.data.local.database.toAccountEntity
import net.techandgraphics.wastical.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastical.data.local.database.toCompanyBinCollectionEntity
import net.techandgraphics.wastical.data.local.database.toCompanyContactEntity
import net.techandgraphics.wastical.data.local.database.toCompanyEntity
import net.techandgraphics.wastical.data.local.database.toCompanyLocationRequest
import net.techandgraphics.wastical.data.local.database.toDemographicAreaEntity
import net.techandgraphics.wastical.data.local.database.toDemographicDistrictEntity
import net.techandgraphics.wastical.data.local.database.toDemographicStreetEntity
import net.techandgraphics.wastical.data.local.database.toNotificationEntity
import net.techandgraphics.wastical.data.local.database.toPaymentCollectionDayEntity
import net.techandgraphics.wastical.data.local.database.toPaymentEntity
import net.techandgraphics.wastical.data.local.database.toPaymentGatewayEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastical.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastical.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastical.data.remote.ServerResponse
import net.techandgraphics.wastical.data.remote.account.AccountApi
import net.techandgraphics.wastical.data.remote.mapApiError
import net.techandgraphics.wastical.getAccount
import javax.inject.Inject

class AccountSessionRepositoryImpl @Inject constructor(
  private val database: AppDatabase,
  private val sessionService: AccountApi,
  private val authenticatorHelper: AuthenticatorHelper,
  private val accountManager: AccountManager,
  private val gson: Gson,
) : AccountSessionRepository {

  private fun getAccount() = authenticatorHelper.getAccount(accountManager)

  override suspend fun fetch(id: Long): ServerResponse = sessionService.get(id)

  override suspend fun fetchSession() {
    getAccount()?.let { account ->
      runCatching { sessionService.get(account.id) }.onFailure { println(mapApiError(it)) }
        .onSuccess { accountSession ->
          runCatching {
            database.withTransaction {
              purseData(accountSession) { _, _ -> }
            }
          }
        }
        .onFailure { println(it) }
    }
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
          .plus(notifications?.size ?: 0)
      }

      data.run {
        paymentGateways?.map { it.toPaymentGatewayEntity() }?.also {
          paymentGatewayDao.upsert(it)
          onProgress(totalItemCount, it.size)
        }
        demographicDistricts?.map { it.toDemographicDistrictEntity() }
          ?.also {
            demographicDistrictDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }
        demographicAreas?.map { it.toDemographicAreaEntity() }
          ?.also {
            demographicAreaDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }
        demographicStreets?.map { it.toDemographicStreetEntity() }
          ?.also {
            demographicStreetDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }
        companies?.map { it.toCompanyEntity() }?.also {
          companyDao.upsert(it)
          onProgress(totalItemCount, it.size)
        }

        companyContacts?.map { it.toCompanyContactEntity() }
          ?.also {
            companyContactDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }

        companyLocations?.map { it.toCompanyLocationRequest() }
          ?.also {
            companyLocationDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }

        companyBinCollections?.map { it.toCompanyBinCollectionEntity() }
          ?.also {
            companyBinCollectionDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }

        accounts?.map { it.toAccountEntity() }
          ?.also { account ->
            accountDao.upsert(account)
            onProgress(totalItemCount, account.size)
          }

        accountContacts?.map { it.toAccountContactEntity() }
          ?.also {
            accountContactDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }
        paymentPlans?.map { it.toPaymentPlanEntity() }?.also {
          paymentPlanDao.upsert(it)
          onProgress(totalItemCount, it.size)
        }
        accountPaymentPlans?.map { it.toAccountPaymentPlanEntity() }
          ?.also {
            accountPaymentPlanDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }
        paymentCollectionDays?.map { it.toPaymentCollectionDayEntity() }
          ?.also {
            paymentCollectionDayDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }

        paymentMethods?.map { it.toPaymentMethodEntity() }
          ?.map { it.copy(isSelected = (it.account == "In Person")) }
          ?.also {
            paymentMethodDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }

        payments?.map { it.toPaymentEntity() }?.also {
          paymentDao.upsert(it)
          onProgress(totalItemCount, it.size)
        }

        paymentMonthsCovered?.map { it.toPaymentMonthCoveredEntity() }
          ?.also {
            paymentMonthCoveredDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }

        notifications?.map { it.toNotificationEntity() }
          ?.also {
            notificationDao.upsert(it)
            onProgress(totalItemCount, it.size)
          }
      }
    }
  }
}
