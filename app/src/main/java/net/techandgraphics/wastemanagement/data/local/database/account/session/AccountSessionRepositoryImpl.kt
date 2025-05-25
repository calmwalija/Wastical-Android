package net.techandgraphics.wastemanagement.data.local.database.account.session

import androidx.room.withTransaction
import kotlinx.coroutines.delay
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toAccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentCollectionDayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.local.database.toStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.toTrashCollectionScheduleEntity
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.onApiErrorHandler
import javax.inject.Inject

class AccountSessionRepositoryImpl @Inject constructor(
  private val database: AppDatabase,
  private val api: AppApi,
) : AccountSessionRepository {

  override suspend fun invoke() {
    delay(2_000)
    runCatching { api.accountSessionApi.get() }
      .onFailure { println(onApiErrorHandler(it)) }
      .onSuccess { accountSession ->
        try {
          with(database) {
            withTransaction {
              accountSession.run {
                districts.map { it.toDistrictEntity() }.also { districtDao.insert(it) }
                areas.map { it.toAreaEntity() }.also { areaDao.insert(it) }
                streets.map { it.toStreetEntity() }.also { streetDao.insert(it) }
                companies.map { it.toCompanyEntity() }.also { companyDao.insert(it) }
                companyContacts.map { it.toCompanyContactEntity() }
                  .also { companyContactDao.insert(it) }
                trashCollections.map { it.toTrashCollectionScheduleEntity() }
                  .also { trashCollectionScheduleDao.insert(it) }
                account.map { it.toAccountEntity() }.also { accountDao.insert(it) }
                accountContacts.map { it.toAccountContactEntity() }
                  .also { accountContactDao.insert(it) }
                plans.map { it.toPaymentPlanEntity() }.also { paymentPlanDao.insert(it) }
                paymentCollectionDays.map { it.toPaymentCollectionDayEntity() }
                  .also { paymentCollectionDayDao.insert(it) }
                methods.toPaymentMethodEntity(gateways).also { paymentMethodDao.insert(it) }
                payments.map { it.toPaymentEntity() }.also { paymentDao.insert(it) }
              }
            }
          }
        } catch (e: Exception) {
          println(e)
        }
      }
  }
}
