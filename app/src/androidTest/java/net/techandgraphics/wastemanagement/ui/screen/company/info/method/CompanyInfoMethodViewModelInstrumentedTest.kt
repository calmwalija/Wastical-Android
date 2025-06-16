package net.techandgraphics.wastemanagement.ui.screen.company.info.method

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import net.techandgraphics.wastemanagement.BaseTest
import net.techandgraphics.wastemanagement.data.local.database.toAccountEntity
import net.techandgraphics.wastemanagement.data.local.database.toCompanyEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentGatewayEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentMethodEntity
import net.techandgraphics.wastemanagement.data.local.database.toPaymentPlanEntity
import net.techandgraphics.wastemanagement.domain.toCompanyUiModel
import net.techandgraphics.wastemanagement.ui.screen.account4Preview
import net.techandgraphics.wastemanagement.ui.screen.company4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicArea4Preview
import net.techandgraphics.wastemanagement.ui.screen.demographicStreet4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentGateway4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentMethod4Preview
import net.techandgraphics.wastemanagement.ui.screen.paymentPlan4Preview
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompanyInfoMethodViewModelInstrumentedTest : BaseTest() {

  private lateinit var viewModel: CompanyInfoMethodViewModel

  override fun populateStaticTestData() {
    super.populateStaticTestData()
    viewModel = CompanyInfoMethodViewModel(database)
  }

  @Test
  fun initialStateIsLoading() = runTest {
    val initialState = viewModel.state.first()
    assertTrue(initialState is CompanyInfoMethodState.Loading)
  }

  // TODO - this test fails
  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun loadCompanyAndPaymentMethods() = runTest {
    val company = company4Preview.toCompanyEntity()
    val gateway = paymentGateway4Preview.toPaymentGatewayEntity()
    val paymentMethod = paymentMethod4Preview.toPaymentMethodEntity()
    val account = account4Preview.toAccountEntity()
    val plan = paymentPlan4Preview.toPaymentPlanEntity()

    val demographicArea = demographicArea4Preview
    val demographicStreet = demographicStreet4Preview

    database.accountDao.insert(account)
    database.paymentPlanDao.insert(plan)
    database.companyDao.insert(company)
    database.paymentGatewayDao.insert(gateway)
    database.paymentMethodDao.insert(paymentMethod)

    viewModel.state.test {
      val loadingState = awaitItem()
      assertTrue(loadingState is CompanyInfoMethodState.Loading)
      val successState = awaitItem() as CompanyInfoMethodState.Success
      assertEquals(company.toCompanyUiModel(), successState.company)
      awaitComplete()
    }
  }

  @Test
  fun loadEmptyCompanyAndPaymentMethods() = runTest {
    viewModel.state.test {
      val loadingState = awaitItem()
      assertTrue(loadingState is CompanyInfoMethodState.Loading)
      cancelAndIgnoreRemainingEvents()
    }
  }
}
