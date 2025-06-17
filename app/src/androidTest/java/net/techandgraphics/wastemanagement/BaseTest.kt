package net.techandgraphics.wastemanagement

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dagger.hilt.android.testing.HiltAndroidRule
import kotlinx.coroutines.runBlocking
import net.techandgraphics.wastemanagement.data.local.database.AppDatabase
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyLocationEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicAreaEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicDistrictEntity
import net.techandgraphics.wastemanagement.data.local.database.toDemographicStreetEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.companyLocation4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicArea4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicDistrict4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentGateway4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentMethod4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import org.junit.After
import org.junit.Before
import org.junit.Rule

abstract class BaseTest {

  protected lateinit var database: AppDatabase
  protected lateinit var context: Context

  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @Before
  fun baseSetup() {
    hiltRule.inject()
    context = ApplicationProvider.getApplicationContext()
    database = Room.inMemoryDatabaseBuilder(
      context,
      AppDatabase::class.java,
    ).allowMainThreadQueries().build()

    tableSeeder()
    populateStaticTestData()
  }

  @After
  fun baseTeardown() {
    database.close()
  }

  protected open fun populateStaticTestData() = Unit

  protected fun tableSeeder() = runBlocking {
    val company = company4Preview.toCompanyEntity()
    val gateway = paymentGateway4Preview.toPaymentGatewayEntity()
    val paymentMethod = paymentMethod4Preview.toPaymentMethodEntity()
    val account = account4Preview.toAccountEntity()
    val plan = paymentPlan4Preview.toPaymentPlanEntity()

    val demographicArea = demographicArea4Preview.toDemographicAreaEntity()
    val demographicStreet = demographicStreet4Preview.toDemographicStreetEntity()
    val demographicDistrict = demographicDistrict4Preview.toDemographicDistrictEntity()
    val companyLocationEntity = companyLocation4Preview.toCompanyLocationEntity()

    database.demographicDistrictDao.insert(demographicDistrict)
    database.demographicStreetDao.insert(demographicStreet)
    database.demographicAreaDao.insert(demographicArea)
    database.companyDao.insert(company)
    database.companyLocationDao.insert(companyLocationEntity)
    database.accountDao.insert(account)
    database.paymentPlanDao.insert(plan)
    database.paymentGatewayDao.insert(gateway)
    database.paymentMethodDao.insert(paymentMethod)
  }
}
