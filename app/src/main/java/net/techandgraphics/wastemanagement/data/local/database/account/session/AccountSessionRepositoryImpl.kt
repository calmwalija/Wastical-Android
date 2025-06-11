package net.techandgraphics.wastemanagement.data.local.database.account.session

import androidx.room.withTransaction
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toAccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.toAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMonthCoveredEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.toStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.toTrashCollectionScheduleEntity
import net.techandgraphics.wastemanagement.data.remote.account.ACCOUNT_ID
import net.techandgraphics.wastemanagement.data.remote.account.AccountApi
import net.techandgraphics.wastemanagement.data.remote.mapApiError
import net.techandgraphics.wastemanagement.data.remote.payment.PaymentType
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
                demographicDistricts?.map { it.toDistrictEntity() }
                  ?.also { demographicDistrictDao.insert(it) }
                demographicAreas?.map { it.toAreaEntity() }?.also { demographicAreaDao.insert(it) }
                demographicStreets?.map { it.toStreetEntity() }
                  ?.also { demographicStreetDao.insert(it) }
                companies?.map { it.toCompanyEntity() }?.also { companyDao.insert(it) }
                companyContacts?.map { it.toCompanyContactEntity() }
                  ?.also { companyContactDao.insert(it) }
                trashCollectionSchedules?.map { it.toTrashCollectionScheduleEntity() }
                  ?.also { trashScheduleDao.insert(it) }
                accounts?.map { it.toAccountEntity() }?.forEach { account ->
                  val streetId = trashScheduleDao.get(account.trashCollectionScheduleId).streetId
                  accountDao.insert(account.copy(streetId = streetId))
                }
                accountContacts?.map { it.toAccountContactEntity() }
                  ?.also { accountContactDao.insert(it) }
                paymentPlans?.map { it.toPaymentPlanEntity() }?.also { paymentPlanDao.insert(it) }
                accountPaymentPlans?.map { it.toAccountPaymentPlanEntity() }
                  ?.also { accountPaymentPlanDao.insert(it) }
                paymentCollectionDays?.map { it.toPaymentCollectionDayEntity() }
                  ?.also { paymentDayDao.insert(it) }

                paymentGateways?.also {
                  paymentMethods
                    ?.toPaymentMethodEntity(it)
                    ?.map { method ->
                      paymentMethodDao.insert(method.copy(isSelected = method.type == PaymentType.Cash.name))
                    }
                }

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
