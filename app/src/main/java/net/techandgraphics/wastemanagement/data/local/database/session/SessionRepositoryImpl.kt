package net.techandgraphics.wastemanagement.data.local.database.session

import androidx.room.withTransaction
import kotlinx.coroutines.delay
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toAccountContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyContactEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastemanagement.data.remote.AppApi
import net.techandgraphics.wastemanagement.data.remote.onApiErrorHandler
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
  private val database: AppDatabase,
  private val api: AppApi,
) : SessionRepository {

  override suspend fun invoke() {
    delay(2_000)
    runCatching { api.sessionApi.get() }
      .onFailure { println(onApiErrorHandler(it)) }
      .onSuccess { session ->
        try {
          with(database) {
            withTransaction {
              session.run {
                company.company.toCompanyEntity().also { companyDao.insert(it) }
                account.account.toAccountEntity().also { accountDao.insert(it) }
                account.contacts.map { it.toAccountContactEntity() }
                  .also { accountContactDao.insert(it) }
                company.contacts.map { it.toCompanyContactEntity() }
                  .also { companyContactDao.insert(it) }
                plans.map { it.toPaymentPlanEntity() }.also { paymentPlanDao.insert(it) }
                methods.map { it.toPaymentMethodEntity() }.also { paymentMethodDao.insert(it) }
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
